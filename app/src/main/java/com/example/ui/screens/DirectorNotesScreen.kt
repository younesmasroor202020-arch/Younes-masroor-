package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.local.DirectorNoteEntity
import com.example.ui.theme.JardanAmberTertiary
import com.example.ui.theme.OchreGoldPrimary
import com.example.ui.theme.PalmOliveSecondary
import com.example.ui.viewmodel.CinematicViewModel

data class ShotCheckItem(
    val sceneNum: Int,
    val name: String,
    val description: String,
    val isCompleted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorNotesScreen(viewModel: CinematicViewModel) {
    val directorNotes by viewModel.directorNotes.collectAsState()
    val isConsulting by viewModel.isConsultingDirector.collectAsState()
    val directorAdvice by viewModel.directorAdvice.collectAsState()
    val selectedScene by viewModel.selectedScene.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var noteShotType by remember { mutableStateOf("Close-up") }

    var directorQuestion by remember {
        mutableStateOf("كيف يمكن تعزيز التباين البصري بين ضوء السراج الزيتي البرتقالي وسماء الليل النيلية في المشهد الثالث لإبراز مشاعر الأب والابن؟")
    }

    var checklistState by remember {
        mutableStateOf(
            listOf(
                ShotCheckItem(1, "Extreme Long Shot (ELS)", "استعراض بساتين الروضة ومنازلها الطينية البعيدة بهبوط متدرج", true),
                ShotCheckItem(1, "Medium Shot (MS)", "اقتراب هادئ على حوامل متحركة من العم يونس وهو يقلّم النخلة", true),
                ShotCheckItem(1, "Close-up (CU)", "تركيز على الأيدي المجعدة المتمرسة تلامس السعف", true),
                ShotCheckItem(1, "Over-the-Shoulder (OTS)", "دخول أصيل بتركيز بؤري متغير من الوجه إلى رسالة الموافقة", true),
                ShotCheckItem(2, "Establishing Tracking", "تجوال بين أزقة السوق الطينية ووجوه أهالي الروضة قبل دخول الدكان", true),
                ShotCheckItem(2, "Macro Shot", "لقطة تفصيلية مبهرة لسكب عسل الجردان والسمسم على الحلوى الروضية", true),
                ShotCheckItem(2, "Medium Two-Shot", "تناغم يونس وأصيل خلف المنضدة الخشبية أثناء تفاعلهما مع الزبائن", true),
                ShotCheckItem(3, "Crane Up Shot", "لقطة رافعة من السطح تلتقط البيوت الطينية والنجوم وتنزل لمستواهما", true),
                ShotCheckItem(3, "Extreme Close-up (ECU)", "عيني أصيل تعكسان ضوء النجوم وسراج الزيت إشارة للقرار واليقين", true),
                ShotCheckItem(3, "360° Orbit Shot", "دوران مستمر وحالم حول الأب والابن لحظة العناق والاتفاق", true)
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("director_notes_screen"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Screen Header
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
                            .background(Color(0xFF5D9CEC).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = Color(0xFF5D9CEC)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "دفتر المخرج واستشارات الإنتاج السينمائي",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "تخطيط حركة الكاميرا وملاحظات التنفيذ (محفوظة في Room)",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }
        }

        // Camera Shot Breakdown Checklist
        item {
            Text(
                text = "خطة اللقطات وحركة الكاميرا المعتمدة بالسيناريو:",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = OchreGoldPrimary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            checklistState.forEachIndexed { index, item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.isCompleted) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isCompleted,
                            onCheckedChange = { checked ->
                                checklistState = checklistState.toMutableList().also {
                                    it[index] = item.copy(isCompleted = checked)
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = OchreGoldPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = OchreGoldPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "المشهد ${item.sceneNum}",
                                        fontSize = 10.sp,
                                        color = OchreGoldPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }

        // AI Director Consultation (gemini-3.5-flash)
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = JardanAmberTertiary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "استشارة المخرج الفني (AI Director - gemini-3.5-flash)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = JardanAmberTertiary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = directorQuestion,
                        onValueChange = { directorQuestion = it },
                        label = { Text("استفسار إخراجي أو فني حول المشهد") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp)
                            .testTag("director_question_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.consultDirector(directorQuestion) },
                        enabled = !isConsulting && directorQuestion.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = JardanAmberTertiary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("submit_director_consult_btn")
                    ) {
                        if (isConsulting) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("جاري طلب استشارة المخرج...", color = Color.Black)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("طلب استشارة إخراجية", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (!directorAdvice.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "توجيهات المخرج السينمائي:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = OchreGoldPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = directorAdvice ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        lineHeight = 20.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Room Database Notes Section
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ملاحظاتي الإخراجية المحفوظة (Room DB):",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = PalmOliveSecondary
                    )
                )

                FilledTonalButton(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PalmOliveSecondary.copy(alpha = 0.2f),
                        contentColor = PalmOliveSecondary
                    ),
                    modifier = Modifier.testTag("add_director_note_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة ملاحظة")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (directorNotes.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا توجد ملاحظات إخراجية مضافة حتى الآن.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = "اضغط على زر «إضافة ملاحظة» لتدوين زوايا التصوير وتوجيهات الممثلين وحفظها محلياً.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }

        items(directorNotes) { note ->
            DirectorNoteItem(
                note = note,
                onDelete = { viewModel.deleteDirectorNote(note) }
            )
        }
    }

    // Add Note Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة ملاحظة إخراجية للمشهد") },
            text = {
                Column {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        label = { Text("عنوان الملاحظة (مثال: ضبط إضاءة الغروب)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteShotType,
                        onValueChange = { noteShotType = it },
                        label = { Text("نوع اللقطة (مثال: Macro, Close-up, Crane)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        label = { Text("تفاصيل الملاحظة الإخراجية") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteTitle.isNotBlank() && noteContent.isNotBlank()) {
                            viewModel.addDirectorNote(noteTitle, noteContent, noteShotType)
                            noteTitle = ""
                            noteContent = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OchreGoldPrimary)
                ) {
                    Text("حفظ الملاحظة", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun DirectorNoteItem(
    note: DirectorNoteEntity,
    onDelete: () -> Unit
) {
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
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = PalmOliveSecondary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = note.shotType,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PalmOliveSecondary
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.note,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف الملاحظة",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
