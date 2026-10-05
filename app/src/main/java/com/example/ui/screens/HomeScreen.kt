package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CinematicScene
import com.example.data.model.CinematicScriptRepository
import com.example.ui.theme.JardanAmberTertiary
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.theme.PalmOliveSecondary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CinematicViewModel

@Composable
fun HomeScreen(
    viewModel: CinematicViewModel,
    onNavigate: (AppScreen) -> Unit,
    onSelectScene: (CinematicScene) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Poster Section
        item {
            HeroPosterSection(
                onReadScript = { onNavigate(AppScreen.SCRIPT) }
            )
        }

        // Quick Feature Cards
        item {
            StudioHubSection(onNavigate = onNavigate)
        }

        // Scene Showcase Header
        item {
            PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مشاهد السيناريو السينمائي (3 مشاهد)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = OchreGoldPrimary
                    )
                )
                TextButton(
                    onClick = { onNavigate(AppScreen.SCRIPT) },
                    modifier = Modifier.testTag("view_all_scenes_btn")
                ) {
                    Text("عرض الكل", color = OchreGoldPrimary)
                }
            }
        }

        // The 3 Scenes List
        items(CinematicScriptRepository.scenes) { scene ->
            SceneCard(
                scene = scene,
                onClick = {
                    onSelectScene(scene)
                    onNavigate(AppScreen.SCRIPT)
                },
                onQuickPlayVoice = {
                    val firstDialogue = scene.dialogues.firstOrNull { it.text.isNotBlank() && it.text != "..." }
                    if (firstDialogue != null) {
                        viewModel.playDialogueLine(firstDialogue)
                    }
                },
                onQuickPlayMusic = {
                    viewModel.generateMusic(
                        prompt = scene.musicPrompt,
                        title = "موسيقى: ${scene.title}"
                    )
                }
            )
        }

        // Characters Section
        item {
            CharactersSection()
        }

        // Real Inspiration / Heritage Tribute
        item {
            HeritageTributeCard()
        }
    }
}

@Composable
fun HeroPosterSection(onReadScript: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .testTag("hero_poster_section")
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_roots_poster),
            contentDescription = "ملصق فيلم جذور في أرض الروضة",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x9916120E),
                            Color(0xF016120E)
                        )
                    )
                )
        )

        // Content on top
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Surface(
                color = OchreGoldPrimary.copy(alpha = 0.25f),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, OchreGoldPrimary.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "سيناريو إخراجي سينمائي أصيل",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(color = OchreGoldPrimary)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "جذور في أرض الروضة",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )

            Text(
                text = "بساتين النخيل، دكان الحلوى الروضية، وسطوح الطين تحت نجوم شبوة",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFDDD2C8)
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onReadScript,
                colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("hero_read_script_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "بدء قراءة السيناريو الإخراجي",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun StudioHubSection(onNavigate: (AppScreen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "استوديوهات الإنتاج الذكي بالذكاء الاصطناعي",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StudioQuickCard(
                modifier = Modifier.weight(1f),
                title = "نطق الحوار",
                subtitle = "gemini-3.8-flash-tts",
                icon = Icons.Default.RecordVoiceOver,
                color = OchreGoldPrimary,
                onClick = { onNavigate(AppScreen.TTS_STUDIO) },
                testTag = "hub_tts_card"
            )

            StudioQuickCard(
                modifier = Modifier.weight(1f),
                title = "توليد الموسيقى",
                subtitle = "lyria-3-clip-preview",
                icon = Icons.Default.MusicNote,
                color = JardanAmberTertiary,
                onClick = { onNavigate(AppScreen.MUSIC_STUDIO) },
                testTag = "hub_music_card"
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StudioQuickCard(
                modifier = Modifier.weight(1f),
                title = "صورة تعبيرية لمشهد",
                subtitle = "gemini-3.1-flash-image",
                icon = Icons.Default.AutoAwesome,
                color = PalmOliveSecondary,
                onClick = { onNavigate(AppScreen.VISUAL_STUDIO) },
                testTag = "hub_visual_card"
            )

            StudioQuickCard(
                modifier = Modifier.weight(1f),
                title = "دفتر المخرج",
                subtitle = "لقطات وملاحظات Room",
                icon = Icons.Default.Videocam,
                color = Color(0xFF5D9CEC),
                onClick = { onNavigate(AppScreen.DIRECTOR_NOTES) },
                testTag = "hub_director_card"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dedicated Banner for Expressive Visual Scene Generation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { onNavigate(AppScreen.VISUAL_STUDIO) }
                .testTag("expressive_scene_banner_card"),
            colors = CardDefaults.cardColors(
                containerColor = PalmOliveSecondary.copy(alpha = 0.15f)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, PalmOliveSecondary.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PalmOliveSecondary.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = PalmOliveSecondary,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "توليد صورة تعبيرية لمشهد بصري",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PalmOliveSecondary
                        )
                    )
                    Text(
                        text = "أدخل وصفك للمشهد، ودع الذكاء الاصطناعي يرسم كادراً سينمائياً تعبيرياً بدقة فائقة.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    tint = PalmOliveSecondary
                )
            }
        }
    }
}

@Composable
fun StudioQuickCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SceneCard(
    scene: CinematicScene,
    onClick: () -> Unit,
    onQuickPlayVoice: () -> Unit,
    onQuickPlayMusic: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("scene_card_${scene.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Image(
                    painter = painterResource(id = scene.defaultImageRes),
                    contentDescription = scene.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Scene number badge
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "المشهد ${scene.id}",
                        color = OchreGoldPrimary,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Quick audio actions on top right
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = onQuickPlayVoice,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.7f),
                            contentColor = OchreGoldPrimary
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.RecordVoiceOver,
                            contentDescription = "استماع للحوار الأول",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    FilledTonalIconButton(
                        onClick = onQuickPlayMusic,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.7f),
                            contentColor = JardanAmberTertiary
                        ),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = "توليد واستماع للموسيقى",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = scene.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = scene.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = OchreGoldPrimary)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = scene.timeAndPlace,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Camera shot tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    scene.cameraShots.take(3).forEach { shot ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = shot.tag,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharactersSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "شخصيات السيناريو وممثلو الأصوات",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OchreGoldPrimary
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        CinematicScriptRepository.characters.forEach { char ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(
                                if (char.id == "younes") OchreGoldPrimary.copy(alpha = 0.25f)
                                else PalmOliveSecondary.copy(alpha = 0.25f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (char.id == "younes") OchreGoldPrimary else PalmOliveSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = char.name,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = char.roleTitle,
                            style = MaterialTheme.typography.labelSmall.copy(color = OchreGoldPrimary)
                        )
                        Text(
                            text = char.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HeritageTributeCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("heritage_tribute_card"),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF26190D)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, OchreGoldPrimary.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = OchreGoldPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "إهداء وإلهام من قلب شبوة — الروضة",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OchreGoldPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "مستوحى من الروابط العائلية الأصيلة بين العم يونس مسرور وابنه الغالي أصيل مسرور. يجسد هذا العمل السينمائي عمق التمسك بالأرض وتجارة الحلوى الروضية وعسل الجردان، وكيف يمكن للطموح المعاصر أن يُزهر بأصله الشبواني العريق دون أن ينسى جذوره الأولى.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFFEDE0D4),
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "«الشجرة التي تتنكر لجذورها تجف مع أول هبة ريح.. أمّا من يحمل أرضه في قلبه، فإنه يزهر أينما حل.»",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OchreGoldPrimary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
