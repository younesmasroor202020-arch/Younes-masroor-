package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val activeTrackTitle: String = "",
    val error: String? = null
)

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var pcmTrack: AudioTrack? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    fun playAudioFromBase64(base64Data: String, mimeType: String, title: String) {
        stop()
        try {
            val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
            if (audioBytes == null || audioBytes.isEmpty()) {
                _playbackState.value = _playbackState.value.copy(error = "الملف الصوتي فارغ")
                return
            }

            // Determine if it's PCM or file format
            if (mimeType.contains("pcm", ignoreCase = true) || mimeType.contains("raw", ignoreCase = true)) {
                playRawPcm(audioBytes, title)
            } else {
                playMediaFileBytes(audioBytes, mimeType, title)
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Failed to play audio", e)
            _playbackState.value = _playbackState.value.copy(
                isPlaying = false,
                error = "فشل تشغيل الصوت: ${e.message}"
            )
        }
    }

    fun playLocalFile(file: File, title: String) {
        stop()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopProgressTracking()
                    _playbackState.value = _playbackState.value.copy(isPlaying = false, currentPositionMs = 0)
                }
            }
            val dur = mediaPlayer?.duration ?: 0
            _playbackState.value = PlaybackState(
                isPlaying = true,
                durationMs = dur,
                activeTrackTitle = title
            )
            startProgressTracking()
        } catch (e: Exception) {
            _playbackState.value = _playbackState.value.copy(error = "فشل تشغيل الملف: ${e.message}")
        }
    }

    private fun playMediaFileBytes(audioBytes: ByteArray, mimeType: String, title: String) {
        try {
            val ext = when {
                mimeType.contains("wav") -> ".wav"
                mimeType.contains("ogg") -> ".ogg"
                else -> ".mp3"
            }
            val tempFile = File.createTempFile("audio_preview_", ext, context.cacheDir)
            tempFile.deleteOnExit()
            FileOutputStream(tempFile).use { it.write(audioBytes) }

            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopProgressTracking()
                    _playbackState.value = _playbackState.value.copy(isPlaying = false, currentPositionMs = 0)
                }
            }
            val dur = mediaPlayer?.duration ?: 0
            _playbackState.value = PlaybackState(
                isPlaying = true,
                durationMs = dur,
                activeTrackTitle = title
            )
            startProgressTracking()
        } catch (e: Exception) {
            // Attempt fallback to WAV header wrapper if it was raw PCM masquerading
            tryPlayAsWav(audioBytes, title)
        }
    }

    private fun tryPlayAsWav(pcmData: ByteArray, title: String) {
        try {
            val wavFile = File.createTempFile("audio_wav_", ".wav", context.cacheDir)
            wavFile.deleteOnExit()
            val wavHeader = createWavHeader(pcmData.size, 24000, 1, 16)
            FileOutputStream(wavFile).use {
                it.write(wavHeader)
                it.write(pcmData)
            }
            mediaPlayer = MediaPlayer().apply {
                setDataSource(wavFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopProgressTracking()
                    _playbackState.value = _playbackState.value.copy(isPlaying = false, currentPositionMs = 0)
                }
            }
            _playbackState.value = PlaybackState(
                isPlaying = true,
                durationMs = mediaPlayer?.duration ?: 0,
                activeTrackTitle = title
            )
            startProgressTracking()
        } catch (e: Exception) {
            playRawPcm(pcmData, title)
        }
    }

    private fun playRawPcm(pcmBytes: ByteArray, title: String, sampleRate: Int = 24000) {
        try {
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(pcmBytes.size)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            pcmTrack = track
            track.play()

            val estimatedDurationMs = ((pcmBytes.size / (sampleRate * 2.0)) * 1000).toInt()
            _playbackState.value = PlaybackState(
                isPlaying = true,
                durationMs = estimatedDurationMs,
                activeTrackTitle = title
            )

            scope.launch(Dispatchers.IO) {
                track.write(pcmBytes, 0, pcmBytes.size)
                delay(estimatedDurationMs.toLong())
                launch(Dispatchers.Main) {
                    stop()
                }
            }
        } catch (e: Exception) {
            _playbackState.value = _playbackState.value.copy(error = "خطأ في تشغيل الصوت: ${e.message}")
        }
    }

    fun pause() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                stopProgressTracking()
                _playbackState.value = _playbackState.value.copy(isPlaying = false)
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "pause error", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
            _playbackState.value = _playbackState.value.copy(isPlaying = true)
            startProgressTracking()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "resume error", e)
        }
    }

    fun stop() {
        stopProgressTracking()
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "stop error", e)
        }
        mediaPlayer = null

        try {
            pcmTrack?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "pcm stop error", e)
        }
        pcmTrack = null

        _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            currentPositionMs = 0
        )
    }

    fun seekTo(positionMs: Int) {
        try {
            mediaPlayer?.seekTo(positionMs)
            _playbackState.value = _playbackState.value.copy(currentPositionMs = positionMs)
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "seek error", e)
        }
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && mediaPlayer?.isPlaying == true) {
                val current = mediaPlayer?.currentPosition ?: 0
                _playbackState.value = _playbackState.value.copy(currentPositionMs = current)
                delay(200)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun createWavHeader(totalAudioLen: Int, sampleRate: Int, channels: Int, bitsPerSample: Int): ByteArray {
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // PCM
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * bitsPerSample / 8).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()
        return header
    }
}
