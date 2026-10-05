package com.example.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CinematicScriptRepository
import com.example.data.remote.GeneratedImageResult
import com.example.ui.theme.JardanAmberTertiary
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.theme.PalmOliveSecondary
import com.example.ui.viewmodel.CinematicViewModel

data class SceneStylePreset(
    val id: String,
    val name: String,
    val promptModifier: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisualStudioScreen(viewModel: CinematicViewModel) {
    val isImageLoading by viewModel.isImageLoading.collectAsState()
    val imageError by viewModel.imageError.collectAsState()
    val generatedImages by viewModel.generatedImages.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0: توليد من الوصف, 1: تعديل مشهد
    var selectedAspectRatio by remember { mutableStateOf("16:9") }
    var selectedStyle by remember { mutableStateOf("cinematic") }

    // User description for visual scene
    var sceneDescription by remember {
        mutableStateOf("بستان نخيل شبواني أصيل في مدينة الروضة وقت الأصيل، جدران طينية منخفضة وأشعة الشمس الذهبية الدافئة تخترق سعف النخيل، ودكة حجرية مغطاة بسجادة يمنية تقليدية وأدوات تقليم النخيل القديمة.")
    }

    // Edit prompt
    var editPrompt by remember {
        mutableStateOf("أضف سراجاً زيتياً عتيقاً يضيء بنور دافئ متوهج على الجدار الطيني مع بريق النجوم في السماء.")
    }

    var selectedImageForEditIndex by remember { mutableStateOf(1) }
    var fullScreenImageResult by remember { mutableStateOf<GeneratedImageResult?>(null) }

    val styles = listOf(
        SceneStylePreset("cinematic", "سينمائي واقعي", "cinematic film still, photorealistic 8k, dramatic lighting, shot on 35mm lens, masterpiece", Icons.Default.MovieFilter),
        SceneStylePreset("oil_painting", "لوحة زيتية تعبيرية", "expressive oil painting on canvas, rich texture, warm earthen palette, artistic brushstrokes", Icons.Default.Palette),
        SceneStylePreset("golden_hour", "ساعة الأصيل الذهبية", "golden hour sunset, warm amber rim light, soft cinematic dust particles, atmospheric glow", Icons.Default.WbSunny),
        SceneStylePreset("starlit", "ليل شبوة وسراج زيتي", "deep midnight indigo desert sky, glittering stars, warm glowing oil lantern light, cinematic contrast", Icons.Default.Nightlight),
        SceneStylePreset("watercolor", "ألوان مائية حالمة", "atmospheric dreamy watercolor, delicate pigment washes, soft edges, evocative lighting", Icons.Default.Brush)
    )

    val scriptInspirations = listOf(
        "بستان النخيل وقت الأصيل" to "بستان نخيل شبواني أصيل في مدينة الروضة وقت الأصيل، جدران طينية منخفضة وأشعة شمس ذهبية تخترق سعف النخيل، دكة حجرية وسجادة يمنية تقليدية، العم يونس وأصيل يتبادلان الحديث بمشاعر وفاء عميقة.",
        "دكان الحلوى وعسل الجردان" to "دكان تراثي عتيق في سوق الروضة القديم، عوارض خشبية سقفية، أوانٍ نحاسية تفيض بعسل الجردان الذهبي والحلوى الروضية الفاخرة وبذور السمسم المحمصة، في إضاءة نهارية دافئة.",
        "سطح المنزل تحت نجوم السماء" to "سطح منزل طيني تقليدي ذو حواف مسننة في مدينة الروضة تحت قبة سماء شبوة المرصعة بآلاف النجوم، سراج زيتي قديم يعطي ضوءاً برتقالياً متراقصاً على وجهي الأب والابن لحظة العناق والقرار المصيري.",
        "يدان مجعدتان ترعيان النخلة" to "لقطة قريبة تعبيرية ليدي العم يونس المجعدتين المتمرستين تلامسان جريد نخلة صغيرة، قطرات ندى وتراب أحمر دافئ، تجسد الارتباط الخالد بالأرض والجذور."
    )

    val atmosphericTags = listOf(
        "إضاءة الأصيل الذهبية",
        "سراج زيتي متوهج",
        "سماء شبوة المرصعة بالنجوم",
        "جدران طينية عتيقة",
        "حلوى روضية وعسل الجردان",
        "ظلال سعف النخيل الناعمة",
        "أجواء سينمائية ملحمية"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("visual_studio_screen"),
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
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PalmOliveSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PalmOliveSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "توليد صورة تعبيرية للمشهد البصري",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "حوّل وصفك الخيالي للمشهد إلى لوحة أو كادر سينمائي مذهل",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Surface(
                            color = PalmOliveSecondary.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "النموذج المعتمد: gemini-3.1-flash-image-preview",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PalmOliveSecondary
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Mode Switcher Tabs
        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PalmOliveSecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .padding(bottom = 14.dp)
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("توليد من وصف المشهد", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Draw, contentDescription = null) },
                    modifier = Modifier.testTag("tab_create_image")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("تعديل مشهد بصري", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AutoFixHigh, contentDescription = null) },
                    modifier = Modifier.testTag("tab_edit_image")
                )
            }
        }

        // Mode 0: Create Expressive Image from Description
        if (activeTab == 0) {
            // Style Selector
            item {
                Text(
                    text = "اختر النمط الفني التعبيري للصورة:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OchreGoldPrimary
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    items(styles) { style ->
                        val isSelected = selectedStyle == style.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedStyle = style.id },
                            label = { Text(style.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = {
                                Icon(
                                    imageVector = style.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) PalmOliveSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.testTag("style_chip_${style.id}")
                        )
                    }
                }
            }

            // Aspect Ratio Selector
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أبعاد الصورة (Aspect Ratio):",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("16:9" to "16:9 عريض", "4:3" to "4:3 كلاسيك", "1:1" to "1:1 مربع").forEach { (ratio, label) ->
                            val isSelected = selectedAspectRatio == ratio
                            Surface(
                                color = if (isSelected) PalmOliveSecondary else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedAspectRatio = ratio }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Quick Inspiration from Script
            item {
                Text(
                    text = "أوصاف تعبيرية مقترحة من مشاهد السيناريو:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    items(scriptInspirations) { (shortTitle, fullDescription) ->
                        AssistChip(
                            onClick = { sceneDescription = fullDescription },
                            label = { Text(shortTitle, fontSize = 11.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(14.dp), tint = JardanAmberTertiary)
                            }
                        )
                    }
                }
            }

            // Description Input Field
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "أدخل وصف المشهد البصري بالتفصيل:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = OchreGoldPrimary)
                            )
                            if (sceneDescription.isNotBlank()) {
                                TextButton(
                                    onClick = { sceneDescription = "" },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("مسح النص", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = sceneDescription,
                            onValueChange = { sceneDescription = it },
                            placeholder = {
                                Text("اكتب وصفك هنا: المكان، الزمان، الألوان، الإضاءة، الشخصيات، والانفعالات...")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .testTag("scene_description_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Atmospheric enhancers tags
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "انقر لإضافة لمسات إخراجية للوصف:",
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(atmosphericTags) { tag ->
                                SuggestionChip(
                                    onClick = {
                                        sceneDescription = if (sceneDescription.isBlank()) tag else "$sceneDescription، مع $tag"
                                    },
                                    label = { Text(tag, fontSize = 10.sp) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Primary Generate Button
                Button(
                    onClick = {
                        val currentStyleModifier = styles.firstOrNull { it.id == selectedStyle }?.promptModifier ?: ""
                        val finalPrompt = "$sceneDescription, $currentStyleModifier"
                        viewModel.generateImage(finalPrompt, selectedAspectRatio)
                    },
                    enabled = !isImageLoading && sceneDescription.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PalmOliveSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_scene_image_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isImageLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "جاري رسم وتوليد الصورة التعبيرية...",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(imageVector = Icons.Default.AutoFixNormal, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "توليد الصورة التعبيرية بالذكاء الاصطناعي",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Mode 1: Edit an existing visual scene
            item {
                Text(
                    text = "اختر أحد مشاهد السيناريو للتعديل عليه:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = OchreGoldPrimary
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    items(CinematicScriptRepository.scenes) { scene ->
                        val isSelected = selectedImageForEditIndex == scene.id
                        Card(
                            modifier = Modifier
                                .width(130.dp)
                                .height(95.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.5.dp else 0.dp,
                                    color = if (isSelected) PalmOliveSecondary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedImageForEditIndex = scene.id },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = scene.defaultImageRes),
                                    contentDescription = scene.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                ) {
                                    Text(
                                        text = scene.title,
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = editPrompt,
                    onValueChange = { editPrompt = it },
                    label = { Text("أدخل تعليمات التعديل التعبيري على المشهد") },
                    placeholder = { Text("مثال: أضف سراجاً زيتياً دافئاً، أو حوّل الإضاءة إلى ليلية زرقاء...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("edit_scene_prompt_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val base64 = generatedImages.firstOrNull()?.base64Data ?: ""
                        viewModel.editImage(editPrompt, base64, selectedAspectRatio)
                    },
                    enabled = !isImageLoading && editPrompt.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PalmOliveSecondary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_scene_edit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isImageLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاري تطبيق التعديل...", color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تطبيق التعديل على المشهد", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Error message if any
        if (imageError != null) {
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
                            text = imageError ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Generated Images Gallery
        item {
            Spacer(modifier = Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "معرض الصور التعبيرية المولدة (${generatedImages.size}):",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PalmOliveSecondary
                    )
                )

                if (generatedImages.isNotEmpty()) {
                    Text(
                        text = "اضغط على الصورة لتكبيرها",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (generatedImages.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "لم يتم توليد أي صور تعبيرية حتى الآن",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "أدخل وصف المشهد البصري أعلاه واضغط على زر التوليد، لترى المشهد مرسوماً بتفاصيله بدقة فائقة.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        items(generatedImages) { imgResult ->
            ExpressiveGeneratedImageCard(
                imgResult = imgResult,
                onViewFull = { fullScreenImageResult = imgResult }
            )
        }
    }

    // Full screen image preview dialog
    fullScreenImageResult?.let { imgResult ->
        val bitmap = remember(imgResult.base64Data) {
            try {
                val bytes = Base64.decode(imgResult.base64Data, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (_: Exception) {
                null
            }
        }

        Dialog(onDismissRequest = { fullScreenImageResult = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "عرض الصورة التعبيرية بالدقة الكاملة",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        IconButton(onClick = { fullScreenImageResult = null }) {
                            Icon(Icons.Default.Close, contentDescription = "إغلاق")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = imgResult.caption ?: "صورة تعبيرية",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 250.dp, max = 400.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }

                    if (!imgResult.caption.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = imgResult.caption,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpressiveGeneratedImageCard(
    imgResult: GeneratedImageResult,
    onViewFull: () -> Unit
) {
    val bitmap = remember(imgResult.base64Data) {
        try {
            val bytes = Base64.decode(imgResult.base64Data, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onViewFull)
            .testTag("expressive_image_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = imgResult.caption ?: "صورة تعبيرية مولدة",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("تعذر عرض الصورة", color = Color.White)
                    }
                }

                // Zoom hint icon
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "تكبير", fontSize = 10.sp, color = Color.White)
                    }
                }
            }

            if (!imgResult.caption.isNullOrBlank()) {
                Text(
                    text = imgResult.caption,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}
