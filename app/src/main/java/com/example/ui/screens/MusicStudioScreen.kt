package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JardanAmberTertiary
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.viewmodel.CinematicViewModel

data class MusicScorePreset(
    val title: String,
    val sceneName: String,
    val arabicDescription: String,
    val modelPrompt: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicStudioScreen(viewModel: CinematicViewModel) {
    val isMusicLoading by viewModel.isMusicLoading.collectAsState()
    val musicError by viewModel.musicError.collectAsState()
    val lastGeneratedMusic by viewModel.lastGeneratedMusic.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    var useProModel by remember { mutableStateOf(false) }
    var customPrompt by remember {
        mutableStateOf("A gentle, nostalgic cinematic Yemeni Shabwani solo Oud soundtrack with ambient desert evening breeze and soft palm rustles, reflecting roots and filial bond.")
    }

    val presets = listOf(
        MusicScorePreset(
            title = "عود أصيل الروضة",
            sceneName = "المشهد 1 (بستان النخيل)",
            arabicDescription = "عزف منفرد على آلة العود بألحان شجية مع حفيف سعف النخيل في أصيل الروضة",
            modelPrompt = "A gentle, nostalgic cinematic Yemeni Shabwani solo Oud soundtrack with ambient desert evening breeze and soft palm rustles, reflecting roots and filial bond."
        ),
        MusicScorePreset(
            title = "إيقاع سوق الروضة والناي",
            sceneName = "المشهد 2 (الدكان العتيق)",
            arabicDescription = "إيقاع شبواني أصيل ممزوج بألحان العود والناي وجلبة سوق الروضة القديم",
            modelPrompt = "A lively traditional Shabwani rhythm blended with authentic Oud and Nay melodies, warm market ambience, and festive heritage tones."
        ),
        MusicScorePreset(
            title = "أوركسترا نجوم شبوة الملحمية",
            sceneName = "المشهد 3 (سطوح المنزل)",
            arabicDescription = "أوركسترا ملحمية شجية تجمع العود والوتريات تحت سماء شبوة المرصعة بالنجوم",
            modelPrompt = "An epic, emotional cinematic orchestral build-up with traditional Yemeni Oud, soaring strings, desert wind, and majestic starlit night ambience."
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("music_studio_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Model Header Badge
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(JardanAmberTertiary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = JardanAmberTertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "استوديو الموسيقى التصويرية والألحان",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Surface(
                            color = JardanAmberTertiary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = if (useProModel) "النموذج المعتمد: lyria-3-pro-preview" else "النموذج المعتمد: lyria-3-clip-preview (30s)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JardanAmberTertiary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Model Type Toggle
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (useProModel) "نمط المقطوعة الكاملة (lyria-3-pro)" else "نمط الكليب السريع حتى 30 ثانية (lyria-3-clip)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "توليد موسيقى أوركسترالية شبوانية متوافقة مع مشاهد السيناريو",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Switch(
                        checked = useProModel,
                        onCheckedChange = { useProModel = it },
                        modifier = Modifier.testTag("toggle_music_model")
                    )
                }
            }
        }

        // Scene Score Presets
        item {
            Text(
                text = "المقطوعات المعتمدة لمشاهد السيناريو الثلاثة:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = JardanAmberTertiary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            presets.forEach { preset ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { customPrompt = preset.modelPrompt }
                        .testTag("preset_${preset.title}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (customPrompt == preset.modelPrompt) {
                            JardanAmberTertiary.copy(alpha = 0.15f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                    border = if (customPrompt == preset.modelPrompt) {
                        androidx.compose.foundation.BorderStroke(1.dp, JardanAmberTertiary)
                    } else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LibraryMusic,
                            contentDescription = null,
                            tint = JardanAmberTertiary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = preset.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = preset.sceneName,
                                style = MaterialTheme.typography.labelSmall.copy(color = OchreGoldPrimary)
                            )
                            Text(
                                text = preset.arabicDescription,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // Custom Prompt input
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "وصف اللحن والموسيقى (Prompt):",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            OutlinedTextField(
                value = customPrompt,
                onValueChange = { customPrompt = it },
                label = { Text("أدخل وصف اللحن أو الموسيقى") },
                placeholder = { Text("مثال: Solo Yemeni Oud with gentle palm breeze...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .testTag("music_prompt_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    viewModel.generateMusic(
                        prompt = customPrompt,
                        usePro = useProModel,
                        title = "موسيقى: ${customPrompt.take(25)}"
                    )
                },
                enabled = !isMusicLoading && customPrompt.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = JardanAmberTertiary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_music_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isMusicLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("جاري توليد اللحن عبر Lyria...", color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.PlayCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (useProModel) "توليد الموسيقى (lyria-3-pro)" else "توليد اللحن (lyria-3-clip-preview)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Error Banner
        if (musicError != null) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = musicError ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Music Player Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = JardanAmberTertiary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مشغل الموسيقى التصويرية",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (lastGeneratedMusic != null) {
                            Surface(
                                color = JardanAmberTertiary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = lastGeneratedMusic?.mimeType ?: "audio/mp3",
                                    style = MaterialTheme.typography.labelSmall.copy(color = JardanAmberTertiary),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (lastGeneratedMusic == null && !playbackState.isPlaying) {
                        Text(
                            text = "اختر أحد ألحان المشاهد أو اكتب وصفك، ثم اضغط على زر التوليد لتأليف المقطوعة الموسيقية والاستماع لها مباشرة.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    } else {
                        Text(
                            text = playbackState.activeTrackTitle.ifEmpty { lastGeneratedMusic?.title ?: "موسيقى شبوة" },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    if (playbackState.isPlaying) {
                                        viewModel.audioPlayer.pause()
                                    } else {
                                        if (lastGeneratedMusic != null) {
                                            viewModel.audioPlayer.playAudioFromBase64(
                                                lastGeneratedMusic!!.base64Data,
                                                lastGeneratedMusic!!.mimeType,
                                                lastGeneratedMusic!!.title
                                            )
                                        } else {
                                            viewModel.audioPlayer.resume()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("music_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                                    contentDescription = if (playbackState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                    tint = JardanAmberTertiary,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            IconButton(
                                onClick = { viewModel.audioPlayer.stop() },
                                modifier = Modifier.testTag("music_stop_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.StopCircle,
                                    contentDescription = "إيقاف",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
