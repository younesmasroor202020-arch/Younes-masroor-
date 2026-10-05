package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerManager
import com.example.audio.PlaybackState
import com.example.data.local.AppDatabase
import com.example.data.local.DirectorNoteEntity
import com.example.data.local.SavedMediaEntity
import com.example.data.model.CinematicScene
import com.example.data.model.CinematicScriptRepository
import com.example.data.model.DialogueLine
import com.example.data.remote.ApiResult
import com.example.data.remote.GeminiService
import com.example.data.remote.GeneratedAudioResult
import com.example.data.remote.GeneratedImageResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

enum class AppScreen {
    HOME,
    SCRIPT,
    TTS_STUDIO,
    MUSIC_STUDIO,
    VISUAL_STUDIO,
    DIRECTOR_NOTES
}

class CinematicViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val directorDao = database.directorDao()
    private val geminiService = GeminiService()
    val audioPlayer = AudioPlayerManager(application)

    val playbackState: StateFlow<PlaybackState> = audioPlayer.playbackState

    val directorNotes: StateFlow<List<DirectorNoteEntity>> = directorDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedMediaList: StateFlow<List<SavedMediaEntity>> = directorDao.getAllMedia()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedScene = MutableStateFlow(CinematicScriptRepository.scenes.first())
    val selectedScene: StateFlow<CinematicScene> = _selectedScene.asStateFlow()

    // TTS Studio State (gemini-3.8-flash-tts)
    private val _isTtsLoading = MutableStateFlow(false)
    val isTtsLoading: StateFlow<Boolean> = _isTtsLoading.asStateFlow()

    private val _ttsError = MutableStateFlow<String?>(null)
    val ttsError: StateFlow<String?> = _ttsError.asStateFlow()

    private val _lastGeneratedTts = MutableStateFlow<GeneratedAudioResult?>(null)
    val lastGeneratedTts: StateFlow<GeneratedAudioResult?> = _lastGeneratedTts.asStateFlow()

    private val _activeVoice = MutableStateFlow("Charon") // Charon (deep elder) or Puck (youthful)
    val activeVoice: StateFlow<String> = _activeVoice.asStateFlow()

    // Music Studio State (lyria-3-clip-preview)
    private val _isMusicLoading = MutableStateFlow(false)
    val isMusicLoading: StateFlow<Boolean> = _isMusicLoading.asStateFlow()

    private val _musicError = MutableStateFlow<String?>(null)
    val musicError: StateFlow<String?> = _musicError.asStateFlow()

    private val _lastGeneratedMusic = MutableStateFlow<GeneratedAudioResult?>(null)
    val lastGeneratedMusic: StateFlow<GeneratedAudioResult?> = _lastGeneratedMusic.asStateFlow()

    // Visual Studio State (gemini-3.1-flash-image-preview)
    private val _isImageLoading = MutableStateFlow(false)
    val isImageLoading: StateFlow<Boolean> = _isImageLoading.asStateFlow()

    private val _imageError = MutableStateFlow<String?>(null)
    val imageError: StateFlow<String?> = _imageError.asStateFlow()

    private val _generatedImages = MutableStateFlow<List<GeneratedImageResult>>(emptyList())
    val generatedImages: StateFlow<List<GeneratedImageResult>> = _generatedImages.asStateFlow()

    // Director Advice State (gemini-3.5-flash)
    private val _isConsultingDirector = MutableStateFlow(false)
    val isConsultingDirector: StateFlow<Boolean> = _isConsultingDirector.asStateFlow()

    private val _directorAdvice = MutableStateFlow<String?>(null)
    val directorAdvice: StateFlow<String?> = _directorAdvice.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectScene(scene: CinematicScene) {
        _selectedScene.value = scene
    }

    fun setVoice(voice: String) {
        _activeVoice.value = voice
    }

    // TTS Call using gemini-3.8-flash-tts
    fun generateSpeech(text: String, voiceName: String = _activeVoice.value, title: String = "تسجيل صوتي") {
        if (text.isBlank()) return
        viewModelScope.launch {
            _isTtsLoading.value = true
            _ttsError.value = null
            when (val result = geminiService.generateSpeech(text, voiceName, title)) {
                is ApiResult.Success -> {
                    _lastGeneratedTts.value = result.data
                    _isTtsLoading.value = false
                    // Auto play
                    audioPlayer.playAudioFromBase64(
                        result.data.base64Data,
                        result.data.mimeType,
                        result.data.title
                    )
                    // Save to Room
                    directorDao.insertMedia(
                        SavedMediaEntity(
                            sceneId = _selectedScene.value.id,
                            type = "TTS",
                            title = title,
                            prompt = text
                        )
                    )
                }
                is ApiResult.Error -> {
                    _ttsError.value = result.message
                    _isTtsLoading.value = false
                }
                else -> Unit
            }
        }
    }

    // Play dialogue line with TTS
    fun playDialogueLine(line: DialogueLine) {
        val voice = line.suggestedVoice
        generateSpeech(
            text = line.text,
            voiceName = voice,
            title = "${line.speaker}: ${line.text.take(25)}..."
        )
    }

    // Music Generation using lyria-3-clip-preview
    fun generateMusic(prompt: String, usePro: Boolean = false, title: String = "موسيقى سينمائية") {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isMusicLoading.value = true
            _musicError.value = null
            when (val result = geminiService.generateMusic(prompt, usePro, title)) {
                is ApiResult.Success -> {
                    _lastGeneratedMusic.value = result.data
                    _isMusicLoading.value = false
                    audioPlayer.playAudioFromBase64(
                        result.data.base64Data,
                        result.data.mimeType,
                        result.data.title
                    )
                    directorDao.insertMedia(
                        SavedMediaEntity(
                            sceneId = _selectedScene.value.id,
                            type = "MUSIC",
                            title = title,
                            prompt = prompt
                        )
                    )
                }
                is ApiResult.Error -> {
                    _musicError.value = result.message
                    _isMusicLoading.value = false
                }
                else -> Unit
            }
        }
    }

    // Image Generation using gemini-3.1-flash-image-preview
    fun generateImage(prompt: String, aspectRatio: String = "16:9") {
        if (prompt.isBlank()) return
        viewModelScope.launch {
            _isImageLoading.value = true
            _imageError.value = null
            when (val result = geminiService.generateImage(prompt, aspectRatio)) {
                is ApiResult.Success -> {
                    _generatedImages.value = listOf(result.data) + _generatedImages.value
                    _isImageLoading.value = false
                    directorDao.insertMedia(
                        SavedMediaEntity(
                            sceneId = _selectedScene.value.id,
                            type = "IMAGE",
                            title = "مشهد بصري: ${prompt.take(25)}",
                            prompt = prompt,
                            imageBase64 = result.data.base64Data
                        )
                    )
                }
                is ApiResult.Error -> {
                    _imageError.value = result.message
                    _isImageLoading.value = false
                }
                else -> Unit
            }
        }
    }

    // Image Editing using gemini-3.1-flash-image-preview
    fun editImage(editPrompt: String, base64Image: String, aspectRatio: String = "16:9") {
        if (editPrompt.isBlank()) return
        viewModelScope.launch {
            _isImageLoading.value = true
            _imageError.value = null
            when (val result = geminiService.editImage(editPrompt, base64Image, aspectRatio)) {
                is ApiResult.Success -> {
                    _generatedImages.value = listOf(result.data) + _generatedImages.value
                    _isImageLoading.value = false
                    directorDao.insertMedia(
                        SavedMediaEntity(
                            sceneId = _selectedScene.value.id,
                            type = "IMAGE",
                            title = "تعديل بصري: ${editPrompt.take(25)}",
                            prompt = editPrompt,
                            imageBase64 = result.data.base64Data
                        )
                    )
                }
                is ApiResult.Error -> {
                    _imageError.value = result.message
                    _isImageLoading.value = false
                }
                else -> Unit
            }
        }
    }

    // Director AI consultation using gemini-3.5-flash
    fun consultDirector(question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            _isConsultingDirector.value = true
            _directorAdvice.value = null
            when (val result = geminiService.consultDirector(_selectedScene.value.title, question)) {
                is ApiResult.Success -> {
                    _directorAdvice.value = result.data
                    _isConsultingDirector.value = false
                }
                is ApiResult.Error -> {
                    _directorAdvice.value = "تعذر الحصول على الاستشارة: ${result.message}"
                    _isConsultingDirector.value = false
                }
                else -> Unit
            }
        }
    }

    // Add note to Room
    fun addDirectorNote(title: String, note: String, shotType: String) {
        if (title.isBlank() || note.isBlank()) return
        viewModelScope.launch {
            directorDao.insertNote(
                DirectorNoteEntity(
                    sceneId = _selectedScene.value.id,
                    title = title,
                    note = note,
                    shotType = shotType
                )
            )
        }
    }

    fun deleteDirectorNote(note: DirectorNoteEntity) {
        viewModelScope.launch {
            directorDao.deleteNote(note)
        }
    }

    fun deleteSavedMedia(media: SavedMediaEntity) {
        viewModelScope.launch {
            directorDao.deleteMedia(media)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
