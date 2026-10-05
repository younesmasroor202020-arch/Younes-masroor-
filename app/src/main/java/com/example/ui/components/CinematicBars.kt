package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.ui.theme.JardanAmberTertiary
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinematicTopBar(
    currentScreen: AppScreen,
    onBackToHome: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "جذور في أرض الروضة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OchreGoldPrimary
                    )
                )
                Text(
                    text = when (currentScreen) {
                        AppScreen.HOME -> "الرئيسية — سيناريو شبوة السينمائي"
                        AppScreen.SCRIPT -> "السيناريو الإخراجي الكامل"
                        AppScreen.TTS_STUDIO -> "استوديو الصوت والنطق (TTS)"
                        AppScreen.MUSIC_STUDIO -> "ألحان وموسيقى شبوانية (Lyria)"
                        AppScreen.VISUAL_STUDIO -> "توليد صورة تعبيرية للمشهد البصري"
                        AppScreen.DIRECTOR_NOTES -> "دفتر المخرج واستشارات AI"
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        },
        navigationIcon = {
            if (currentScreen != AppScreen.HOME) {
                IconButton(
                    onClick = onBackToHome,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "العودة للرئيسية",
                        tint = OchreGoldPrimary
                    )
                }
            } else {
                IconButton(
                    onClick = {},
                    modifier = Modifier.testTag("app_logo_icon")
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = "أيقونة السيناريو",
                        tint = OchreGoldPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun CinematicBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "الرئيسية") },
            label = { Text("الرئيسية", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_home")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.SCRIPT,
            onClick = { onNavigate(AppScreen.SCRIPT) },
            icon = { Icon(Icons.Default.MenuBook, contentDescription = "السيناريو") },
            label = { Text("السيناريو", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_script")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.TTS_STUDIO,
            onClick = { onNavigate(AppScreen.TTS_STUDIO) },
            icon = { Icon(Icons.Default.RecordVoiceOver, contentDescription = "الصوت") },
            label = { Text("نطق الحوار", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_tts")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.MUSIC_STUDIO,
            onClick = { onNavigate(AppScreen.MUSIC_STUDIO) },
            icon = { Icon(Icons.Default.MusicNote, contentDescription = "الموسيقى") },
            label = { Text("الموسيقى", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_music")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.VISUAL_STUDIO,
            onClick = { onNavigate(AppScreen.VISUAL_STUDIO) },
            icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "توليد المشهد البصري") },
            label = { Text("المشهد البصري", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_visual")
        )
        NavigationBarItem(
            selected = currentScreen == AppScreen.DIRECTOR_NOTES,
            onClick = { onNavigate(AppScreen.DIRECTOR_NOTES) },
            icon = { Icon(Icons.Default.Videocam, contentDescription = "دفتر المخرج") },
            label = { Text("المخرج", fontSize = 11.sp) },
            modifier = Modifier.testTag("nav_director")
        )
    }
}

@Composable
fun FloatingAudioBar(
    playbackState: PlaybackState,
    onPlayPause: () -> Unit,
    onStop: () -> Unit
) {
    val isVisible = playbackState.isPlaying || playbackState.activeTrackTitle.isNotEmpty()

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .testTag("floating_audio_bar"),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(OchreGoldPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.GraphicEq else Icons.Default.Audiotrack,
                                contentDescription = null,
                                tint = OchreGoldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playbackState.activeTrackTitle.ifEmpty { "صوت قيد التشغيل" },
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val progressText = formatDuration(playbackState.currentPositionMs) + " / " + formatDuration(playbackState.durationMs)
                            Text(
                                text = progressText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onPlayPause,
                            modifier = Modifier.testTag("audio_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (playbackState.isPlaying) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = if (playbackState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                tint = OchreGoldPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        IconButton(
                            onClick = onStop,
                            modifier = Modifier.testTag("audio_stop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق المشغل",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (playbackState.durationMs > 0) {
                    val progress = (playbackState.currentPositionMs.toFloat() / playbackState.durationMs.toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(3.dp),
                        color = OchreGoldPrimary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

private fun formatDuration(millis: Int): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
