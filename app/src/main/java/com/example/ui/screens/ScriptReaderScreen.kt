package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CameraShot
import com.example.data.model.CinematicScene
import com.example.data.model.CinematicScriptRepository
import com.example.data.model.DialogueLine
import com.example.ui.theme.JardanAmberTertiary
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.theme.PalmOliveSecondary
import com.example.ui.viewmodel.CinematicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptReaderScreen(
    viewModel: CinematicViewModel
) {
    val selectedScene by viewModel.selectedScene.collectAsState()
    val isTtsLoading by viewModel.isTtsLoading.collectAsState()
    val isMusicLoading by viewModel.isMusicLoading.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    var activeTab by remember { mutableStateOf(selectedScene.id - 1) }

    LaunchedEffect(selectedScene) {
        activeTab = (selectedScene.id - 1).coerceIn(0, CinematicScriptRepository.scenes.size - 1)
    }

    val currentScene = CinematicScriptRepository.scenes[activeTab]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("script_reader_screen")
    ) {
        // Scene Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = OchreGoldPrimary,
            edgePadding = 12.dp
        ) {
            CinematicScriptRepository.scenes.forEachIndexed { index, scene ->
                Tab(
                    selected = activeTab == index,
                    onClick = {
                        activeTab = index
                        viewModel.selectScene(scene)
                    },
                    text = {
                        Text(
                            text = "المشهد ${scene.id}",
                            fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("script_tab_${scene.id}")
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
        ) {
            // Scene Header Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = currentScene.defaultImageRes),
                            contentDescription = currentScene.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.45f))
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = currentScene.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = currentScene.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(color = OchreGoldPrimary)
                            )
                            Text(
                                text = currentScene.timeAndPlace,
                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFDDD2C8))
                            )
                        }
                    }
                }
            }

            // Decor & Location
            item {
                DirectorialSectionCard(
                    title = "الديكور والمكان",
                    icon = Icons.Default.LocationOn,
                    content = currentScene.decorAndLocation,
                    accentColor = OchreGoldPrimary
                )
            }

            // Lighting & Color Palette
            item {
                DirectorialSectionCard(
                    title = "الإضاءة والألوان (Golden Hour & Cinema Contrast)",
                    icon = Icons.Default.WbSunny,
                    content = currentScene.lightingAndColors,
                    accentColor = JardanAmberTertiary
                )
            }

            // Camera Shots & Movements
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = PalmOliveSecondary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "حركة الكاميرا واللقطات السينمائية",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PalmOliveSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        currentScene.cameraShots.forEach { shot ->
                            CameraShotItem(shot = shot)
                        }
                    }
                }
            }

            // Sound & Music Cue
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = null,
                                    tint = OchreGoldPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "الصوت والموسيقى التصويرية",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OchreGoldPrimary
                                    )
                                )
                            }

                            FilledTonalButton(
                                onClick = {
                                    viewModel.generateMusic(
                                        prompt = currentScene.musicPrompt,
                                        title = "موسيقى: ${currentScene.title}"
                                    )
                                },
                                enabled = !isMusicLoading,
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = OchreGoldPrimary.copy(alpha = 0.2f),
                                    contentColor = OchreGoldPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("script_play_music_cue_btn")
                            ) {
                                if (isMusicLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = OchreGoldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("جاري التوليد...", fontSize = 11.sp)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("توليد اللحن (Lyria)", fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentScene.soundAndMusic,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            // Dialogue & Performance Section
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الحوار والأداء التمثيلي (مع نطق TTS بالذكاء الاصطناعي)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = OchreGoldPrimary
                        )
                    )
                }
            }

            itemsIndexed(currentScene.dialogues) { index, line ->
                DialogueLineCard(
                    line = line,
                    onPlayTts = {
                        viewModel.playDialogueLine(line)
                    },
                    isCurrentLinePlaying = playbackState.isPlaying && playbackState.activeTrackTitle.contains(line.speaker),
                    isLoading = isTtsLoading
                )
            }

            // Thematic core footer
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "الجوهر الفلسفي للمشهد:",
                            style = MaterialTheme.typography.labelSmall.copy(color = OchreGoldPrimary, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentScene.thematicCore,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DirectorialSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
fun CameraShotItem(shot: CameraShot) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            color = PalmOliveSecondary.copy(alpha = 0.2f),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = shot.tag,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PalmOliveSecondary
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = shot.type,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Text(
                text = shot.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun DialogueLineCard(
    line: DialogueLine,
    onPlayTts: () -> Unit,
    isCurrentLinePlaying: Boolean,
    isLoading: Boolean
) {
    val isYounes = line.characterId == "younes"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .testTag("dialogue_card_${line.speaker}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentLinePlaying) {
                OchreGoldPrimary.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isCurrentLinePlaying) {
            androidx.compose.foundation.BorderStroke(1.5.dp, OchreGoldPrimary)
        } else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header with Speaker & Audio Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isYounes) OchreGoldPrimary.copy(alpha = 0.25f) else PalmOliveSecondary.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = line.speaker,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isYounes) OchreGoldPrimary else PalmOliveSecondary
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "صوت: ${line.suggestedVoice}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (line.text.isNotBlank() && line.text != "...") {
                    IconButton(
                        onClick = onPlayTts,
                        modifier = Modifier.testTag("tts_play_${line.speaker}")
                    ) {
                        Icon(
                            imageVector = if (isCurrentLinePlaying) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
                            contentDescription = "استماع للحوار (gemini-3.8-flash-tts)",
                            tint = if (isCurrentLinePlaying) OchreGoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Stage direction if present
            if (line.stageDirection.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "(${line.stageDirection})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontStyle = FontStyle.Italic,
                        color = OchreGoldPrimary.copy(alpha = 0.85f)
                    )
                )
            }

            // Dialogue line text
            if (line.text.isNotBlank() && line.text != "...") {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = line.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
