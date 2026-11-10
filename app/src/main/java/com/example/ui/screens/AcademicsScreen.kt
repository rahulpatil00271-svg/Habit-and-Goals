package com.example.ui.screens

import androidx.compose.foundation.*
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
import com.example.data.model.AcademicSubjectEntity
import com.example.data.model.StudySessionEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.components.StatBox
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AcademicsScreen(
    viewModel: SamuraiViewModel,
    onOpenQuickAdd: () -> Unit
) {
    val subjects by viewModel.subjectsFlow.collectAsState(initial = emptyList())
    val studySessions by viewModel.studySessionsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = uiState.todayDateStr

    // Live Study Stopwatch Timer State
    var isTimerRunning by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var selectedSubjectForTimer by remember { mutableStateOf("") }
    var timerTopicInput by remember { mutableStateOf("") }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val totalStudyMinutesAllTime = remember(studySessions) { studySessions.sumOf { it.durationMinutes } }
    val todayStudyMinutes = remember(studySessions, todayStr) {
        studySessions.filter { it.dateStr == todayStr }.sumOf { it.durationMinutes }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_academics"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KNOWLEDGE TEMPLE",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                                color = MistCyan
                            )
                            Text(
                                text = "Academic & Scholarly Mastery",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = onOpenQuickAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = MistCyan),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Log Session", tint = DemonSlayerBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Session", color = DemonSlayerBlack, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox("Today's Study", "${todayStudyMinutes / 60}h ${todayStudyMinutes % 60}m", MistCyan, Modifier.weight(1f))
                        StatBox("All-Time Study", "${totalStudyMinutesAllTime / 60} Hours", FlameGold, Modifier.weight(1f))
                        StatBox("Subjects", "${subjects.size}", WaterBlue, Modifier.weight(1f))
                    }
                }
            }
        }

        // Live Study Stopwatch Dojo Timer
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, MistCyan.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⏱️ LIVE STUDY STOPWATCH & FOCUS TIMER",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = MistCyan
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val hours = elapsedSeconds / 3600
                    val minutes = (elapsedSeconds % 3600) / 60
                    val seconds = elapsedSeconds % 60
                    Text(
                        text = String.format("%02d:%02d:%02d", hours, minutes, seconds),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = CinzelFontFamily
                        ),
                        color = if (isTimerRunning) MistCyan else TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = timerTopicInput,
                        onValueChange = { timerTopicInput = it },
                        placeholder = { Text("What topic are you mastering right now?") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { isTimerRunning = !isTimerRunning },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTimerRunning) CrimsonSun else MistCyan
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Timer Toggle",
                                tint = DemonSlayerBlack
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isTimerRunning) "Pause Focus" else "Start Focus",
                                color = DemonSlayerBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (elapsedSeconds > 0) {
                            Button(
                                onClick = {
                                    val durationMins = (elapsedSeconds / 60).coerceAtLeast(1)
                                    val subjName = if (subjects.isNotEmpty()) subjects.first().name else "General Studies"
                                    viewModel.logStudySession(
                                        subjectName = subjName,
                                        topic = if (timerTopicInput.isBlank()) "Deep Focus Session" else timerTopicInput.trim(),
                                        durationMinutes = durationMins,
                                        notes = "Recorded via Live Stopwatch",
                                        sessionType = "SELF_STUDY"
                                    )
                                    isTimerRunning = false
                                    elapsedSeconds = 0
                                    timerTopicInput = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FlameGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save & Log", color = DemonSlayerBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Subjects List
        item {
            Text(
                text = "📚 CURRICULUM SUBJECTS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = CinzelFontFamily,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = FlameGold
            )
        }

        items(subjects) { subject ->
            val subjectSessions = studySessions.filter { it.subjectName == subject.name }
            val totalMins = subjectSessions.sumOf { it.durationMinutes }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = subject.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Code: ${subject.code} • Target: ${subject.targetWeeklyHours}h/week",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MistCyan.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${totalMins / 60}h ${totalMins % 60}m Logged",
                                style = MaterialTheme.typography.labelSmall.copy(color = MistCyan, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    val topicProgress = if (subject.totalTopics > 0) subject.completedTopics.toFloat() / subject.totalTopics else 0f
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Topics Mastery: ${subject.completedTopics}/${subject.totalTopics}", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text("${(topicProgress * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { topicProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MistCyan,
                        trackColor = DemonSlayerDark
                    )
                }
            }
        }

        // Recent Study Sessions History
        item {
            Text(
                text = "📜 RECENT STUDY SESSIONS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = CinzelFontFamily,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = FlameGold
            )
        }

        if (studySessions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No study sessions logged yet.", color = TextSecondary)
                    }
                }
            }
        } else {
            items(studySessions.take(5)) { session ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${session.subjectName}: ${session.topic}",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Date: ${session.dateStr} • ${session.durationMinutes} Minutes",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
