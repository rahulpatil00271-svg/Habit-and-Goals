package com.example.ui.screens

import androidx.compose.foundation.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SamuraiViewModel
import com.example.ui.components.PerformanceRow
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen(
    viewModel: SamuraiViewModel
) {
    var selectedPeriod by remember { mutableStateOf("LAST_7_DAYS") } // TODAY, LAST_7_DAYS, THIS_WEEK, THIS_MONTH, LAST_6_MONTHS, THIS_YEAR

    val habits by viewModel.habitsFlow.collectAsState(initial = emptyList())
    val habitLogs by viewModel.habitLogsFlow.collectAsState(initial = emptyList())
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val goals by viewModel.goalsFlow.collectAsState(initial = emptyList())
    val studySessions by viewModel.studySessionsFlow.collectAsState(initial = emptyList())
    val workouts by viewModel.workoutsFlow.collectAsState(initial = emptyList())
    val journals by viewModel.journalsFlow.collectAsState(initial = emptyList())
    val healthRecords by viewModel.healthRecordsFlow.collectAsState(initial = emptyList())
    val journeyLogs by viewModel.journeyLogsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()

    // Real calculations
    val totalHabitCompletions = remember(habitLogs) { habitLogs.count { it.completed } }
    val totalTasksCompleted = remember(tasks) { tasks.count { it.isCompleted } }
    val totalStudyHours = remember(studySessions) { studySessions.sumOf { it.durationMinutes } / 60f }
    val totalWorkouts = remember(workouts) { workouts.size }
    val totalJournals = remember(journals) { journals.size }

    // Normalized Category Progress (0-100)
    val habitScore = if (habits.isNotEmpty()) ((totalHabitCompletions.toFloat() / (habits.size * 14).coerceAtLeast(1)) * 100f).coerceIn(0f, 100f) else 0f
    val taskScore = if (tasks.isNotEmpty()) ((totalTasksCompleted.toFloat() / tasks.size) * 100f).coerceIn(0f, 100f) else 100f
    val studyScore = ((totalStudyHours / 20f) * 100f).coerceIn(0f, 100f)
    val workoutScore = ((totalWorkouts.toFloat() / 15f) * 100f).coerceIn(0f, 100f)
    val reflectionScore = ((totalJournals.toFloat() / 10f) * 100f).coerceIn(0f, 100f)

    // Composite Life Score
    val overallLifeScore = ((habitScore + taskScore + studyScore + workoutScore + reflectionScore) / 5f).toInt()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_analytics"),
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
                    Text(
                        text = "PROGRESS OBSERVATORY",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = ThunderYellow
                    )
                    Text(
                        text = "Global Life Growth & Analytics",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Period Selector
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("TODAY", "LAST_7_DAYS", "THIS_MONTH", "LAST_6_MONTHS", "THIS_YEAR")) { p ->
                            val isSelected = selectedPeriod == p
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) ThunderYellow.copy(alpha = 0.2f) else DemonSlayerDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ThunderYellow else DemonSlayerBorder),
                                modifier = Modifier.clickable { selectedPeriod = p }
                            ) {
                                Text(
                                    text = p.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) ThunderGold else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Composite Life Index Score Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FlameGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "COMPOSITE LIFE INDEX",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                            color = FlameGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$overallLifeScore / 100",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = CinzelFontFamily
                            ),
                            color = TextPrimary
                        )
                        Text(
                            text = "Transparently normalized from actual daily habits, completed tasks, recorded study hours, and workout sessions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(FlameGold.copy(alpha = 0.15f))
                            .border(1.5.dp, FlameGold, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔥", fontSize = 36.sp)
                    }
                }
            }
        }

        // Real Interactive 7-Day Performance Bar Chart
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "7-DAY ACTIVITY INTENSITY",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )
                    Text(
                        text = "Total daily operations (Habits + Tasks + Sessions)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Compute last 7 days metrics
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val labelFormat = SimpleDateFormat("EEE", Locale.US)

                    val daysData = remember(habitLogs, tasks, studySessions, workouts) {
                        val list = mutableListOf<Triple<String, String, Int>>()
                        val c = Calendar.getInstance()
                        c.add(Calendar.DAY_OF_YEAR, -6)
                        for (i in 0..6) {
                            val dStr = dateFormat.format(c.time)
                            val lbl = labelFormat.format(c.time)
                            val count = habitLogs.count { it.dateStr == dStr && it.completed } +
                                    tasks.count { it.dueDateStr == dStr && it.isCompleted } +
                                    studySessions.count { it.dateStr == dStr } +
                                    workouts.count { it.dateStr == dStr }
                            list.add(Triple(dStr, lbl, count))
                            c.add(Calendar.DAY_OF_YEAR, 1)
                        }
                        list
                    }

                    val maxCount = daysData.maxOfOrNull { it.third }?.coerceAtLeast(4) ?: 4

                    // Canvas Bar Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height - 24.dp.toPx()
                            val barWidth = w / 7f * 0.55f
                            val step = w / 7f

                            daysData.forEachIndexed { idx, (_, _, count) ->
                                val barHeight = (count.toFloat() / maxCount) * h
                                val x = idx * step + (step - barWidth) / 2
                                val y = h - barHeight

                                // Draw bar
                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        listOf(CrimsonSunGlow, CrimsonSun)
                                    ),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight.coerceAtLeast(6f)),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                                )
                            }
                        }

                        // Labels row at bottom of chart
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                        ) {
                            daysData.forEach { (_, lbl, count) ->
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = FlameGold
                                    )
                                    Text(
                                        text = lbl,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Breakdown Bars
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "DIMENSIONAL DEVELOPMENT BREAKDOWN",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )

                    PerformanceRow("Disciplines & Habits", "${habitScore.toInt()}%", habitScore / 100f, CrimsonSun)
                    PerformanceRow("Tactical Missions & Tasks", "${taskScore.toInt()}%", taskScore / 100f, FlameGold)
                    PerformanceRow("Academic & Knowledge Cultivation", "${studyScore.toInt()}%", studyScore / 100f, MistCyan)
                    PerformanceRow("Physical Strength & Calisthenics", "${workoutScore.toInt()}%", workoutScore / 100f, WindEmerald)
                    PerformanceRow("Inner Reflection & Mindfulness", "${reflectionScore.toInt()}%", reflectionScore / 100f, WaterBlue)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
