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

data class VoiceOption(val id: String, val name: String, val characterRole: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TtsStudioScreen(viewModel: CinematicViewModel) {
    val isTtsLoading by viewModel.isTtsLoading.collectAsState()
    val ttsError by viewModel.ttsError.collectAsState()
    val lastGeneratedTts by viewModel.lastGeneratedTts.collectAsState()
    val activeVoice by viewModel.activeVoice.collectAsState()
    val playbackState by viewModel.playbackState.collectAsState()

    var inputDialogueText by remember {
        mutableStateOf("الثقل الذي تشعر به يا بني ليس خوفاً، بل هو الوفاء... الروضة ليست مجرد بيوت من طين ونخيل، الروضة هي جذورك، والجذور دائماً تشدّ صاحبها إلى الأرض التي نمت فيها روحه.")
    }

    val voices = listOf(
        VoiceOption("Charon", "Charon", "العم يونس (وقور وحكيم)"),
        VoiceOption("Puck", "Puck", "أصيل مسرور (شاب طموح)"),
        VoiceOption("Kore", "Kore", "الراوي والوصف السينمائي"),
        VoiceOption("Fenrir", "Fenrir", "صوت جهوري عميق"),
        VoiceOption("Aoede", "Aoede", "صوت شجي ناعم")
    )

    val presetQuotes = listOf(
        "الثقل الذي تشعر به يا بني ليس خوفاً، بل هو الوفاء... الروضة هي جذورك.",
        "الشجرة التي تتنكر لجذورها تجف مع أول هبة ريح.. أمّا من يحمل أرضه في قلبه فإنه يزهر أينما حل.",
        "سأسافر للتدريب لعام واحد فقط ثم سأعود فوراً... لنضع اسم مسرور والحلوى الروضية في كل مكان.",
        "هذا هو أصيل الذي عرفته ورعيته... يحلق بعيداً بفكره، ويبقى قلبه ثابتاً في أرض الروضة.",
        "نحن لا نبيع مجرد حلوى أو نزرع مجرد أرض... نحن نحفظ هوية وكرامة عائلة مسرور التي عُرفت بالصدق والجود."
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("tts_studio_screen"),
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
                            .background(OchreGoldPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = OchreGoldPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "استوديو تحويل النص إلى صوت (TTS)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(
                            color = OchreGoldPrimary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "النموذج المعتمد: gemini-3.8-flash-tts",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OchreGoldPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Voice Selector
        item {
            Text(
                text = "اختر نبرة الصوت والممثل الصوتي:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OchreGoldPrimary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                items(voices) { voice ->
                    val isSelected = activeVoice == voice.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setVoice(voice.id) },
                        label = {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = voice.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = voice.characterRole,
                                    fontSize = 10.sp,
                                    color = if (isSelected) OchreGoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null,
                        modifier = Modifier.testTag("voice_chip_${voice.id}")
                    )
                }
            }
        }

        // Preset Script Quotes
        item {
            Text(
                text = "عبارات مختارة من حوارات السيناريو:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                items(presetQuotes) { quote ->
                    AssistChip(
                        onClick = { inputDialogueText = quote },
                        label = {
                            Text(
                                text = quote.take(30) + "...",
                                maxLines = 1,
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    )
                }
            }
        }

        // Text input area
        item {
            OutlinedTextField(
                value = inputDialogueText,
                onValueChange = { inputDialogueText = it },
                label = { Text("أدخل نص الحوار أو الاتجاه الإخراجي") },
                placeholder = { Text("اكتب الحوار باللغة العربية لنطقه...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .testTag("tts_text_input"),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    viewModel.generateSpeech(
                        text = inputDialogueText,
                        voiceName = activeVoice,
                        title = inputDialogueText.take(30)
                    )
                },
                enabled = !isTtsLoading && inputDialogueText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("generate_tts_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isTtsLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("جاري تحويل النص إلى صوت (TTS)...", color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("نطق الحوار بواسطة gemini-3.8-flash-tts", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Error message if any
        if (ttsError != null) {
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
                            text = ttsError ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Last Generated Audio Player Card
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
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = OchreGoldPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مشغل الحوار الصوتي",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (lastGeneratedTts != null) {
                            Surface(
                                color = OchreGoldPrimary.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = lastGeneratedTts?.mimeType ?: "audio",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OchreGoldPrimary),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (lastGeneratedTts == null && !playbackState.isPlaying) {
                        Text(
                            text = "لم يتم توليد أي مقطع صوتي بعد. اضغط على زر النطق أعلاه لبدء الاستماع للحوار بصوت الشخصيات.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    } else {
                        Text(
                            text = playbackState.activeTrackTitle.ifEmpty { lastGeneratedTts?.title ?: "حوار الروضة" },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

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
                                        if (lastGeneratedTts != null) {
                                            viewModel.audioPlayer.playAudioFromBase64(
                                                lastGeneratedTts!!.base64Data,
                                                lastGeneratedTts!!.mimeType,
                                                lastGeneratedTts!!.title
                                            )
                                        } else {
                                            viewModel.audioPlayer.resume()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(54.dp)
                                    .testTag("tts_studio_play_button")
                            ) {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Default.PauseCircleFilled else Icons.Default.PlayCircleFilled,
                                    contentDescription = if (playbackState.isPlaying) "إيقاف" else "تشغيل",
                                    tint = OchreGoldPrimary,
                                    modifier = Modifier.size(50.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            IconButton(
                                onClick = { viewModel.audioPlayer.stop() },
                                modifier = Modifier.testTag("tts_studio_stop_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.StopCircle,
                                    contentDescription = "إيقاف كامل",
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
