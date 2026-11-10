package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HabitDetailScreen(
    habitId: Long,
    viewModel: SamuraiViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val allHabits by viewModel.habitsFlow.collectAsState(initial = emptyList())
    val habit = remember(allHabits, habitId) { allHabits.find { it.id == habitId } }
    val habitLogs by viewModel.habitLogsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = uiState.todayDateStr

    val logsForHabit = remember(habitLogs, habitId) { habitLogs.filter { it.habitId == habitId } }
    val completedDates = remember(logsForHabit) { logsForHabit.filter { it.completed }.map { it.dateStr }.toSet() }

    // Statistics
    val totalCompleted = completedDates.size
    val currentStreak = remember(logsForHabit, todayStr) { calculateStreak(logsForHabit, todayStr) }
    val longestStreak = remember(completedDates) { calculateLongestStreak(completedDates) }

    // Month navigator for this habit
    val cal = remember { Calendar.getInstance() }
    var displayedYear by remember { mutableStateOf(cal.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableStateOf(cal.get(Calendar.MONTH)) }

    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.US) }
    val displayCal = remember(displayedYear, displayedMonth) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, displayedYear)
        c.set(Calendar.MONTH, displayedMonth)
        c.set(Calendar.DAY_OF_MONTH, 1)
        c
    }
    val daysInMonth = displayCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = displayCal.get(Calendar.DAY_OF_WEEK)

    var inspectDate by remember { mutableStateOf(todayStr) }
    var inspectDateNote by remember { mutableStateOf("") }

    if (habit == null) {
        Box(modifier = Modifier.fillMaxSize().background(DemonSlayerBlack), contentAlignment = Alignment.Center) {
            Text("Habit not found", color = TextPrimary)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_habit_detail"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Navigation Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.clip(CircleShape).background(DemonSlayerCard)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "HISTORICAL HABIT DOSSIER",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                        color = TextPrimary
                    )
                }
            }
        }

        // 2. Statistics Grid
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DISCIPLINE METRICS",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily),
                        color = FlameGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBox("Current Streak", "$currentStreak Days", FlameGold, Modifier.weight(1f))
                        StatBox("Longest Streak", "$longestStreak Days", CrimsonSun, Modifier.weight(1f))
                        StatBox("Total Cleared", "$totalCompleted Reps", WaterBlue, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatBox("Target", "${habit.dailyTarget} ${habit.unit}/day", TextPrimary, Modifier.weight(1f))
                        StatBox("Category", habit.category, MistCyan, Modifier.weight(1f))
                        StatBox("Priority", habit.priority, if (habit.priority == "HIGH") CrimsonSunGlow else TextSecondary, Modifier.weight(1f))
                    }
                }
            }
        }

        // 3. GitHub-Style 52-Week Contribution Heatmap (Mandatory Section 07)
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
                        Text(
                            text = "ANNUAL CONSISTENCY HEATMAP",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                            color = FlameGold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Less", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TextTertiary)
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(DemonSlayerCardElevated))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(HeatmapLevel2))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(CrimsonSun))
                            Text("More", style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp), color = TextTertiary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 16 weeks scrollable horizontal heatmap grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val heatmapCal = Calendar.getInstance()
                        // 24 weeks back
                        heatmapCal.add(Calendar.WEEK_OF_YEAR, -20)

                        for (w in 0..20) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                for (d in 0..6) {
                                    val dateStr = dateFormat.format(heatmapCal.time)
                                    val isCompleted = completedDates.contains(dateStr)
                                    val isSelected = inspectDate == dateStr

                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                when {
                                                    isCompleted -> CrimsonSun
                                                    else -> DemonSlayerCardElevated
                                                }
                                            )
                                            .border(
                                                if (isSelected) 1.dp else 0.dp,
                                                if (isSelected) FlameGold else Color.Transparent,
                                                RoundedCornerShape(3.dp)
                                            )
                                            .clickable {
                                                inspectDate = dateStr
                                            }
                                    )
                                    heatmapCal.add(Calendar.DAY_OF_YEAR, 1)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Showing last 20 weeks of activity • Tap any cell to inspect or record past date",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = TextTertiary
                    )
                }
            }
        }

        // 4. Individual Habit Monthly Calendar
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
                        Text(
                            text = monthFormat.format(displayCal.time),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                            color = TextPrimary
                        )
                        Row {
                            IconButton(onClick = {
                                if (displayedMonth == 0) {
                                    displayedMonth = 11
                                    displayedYear--
                                } else displayedMonth--
                            }) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = TextPrimary)
                            }
                            IconButton(onClick = {
                                if (displayedMonth == 11) {
                                    displayedMonth = 0
                                    displayedYear++
                                } else displayedMonth++
                            }) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days of week
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                            Text(
                                text = d,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextSecondary,
                                modifier = Modifier.weight(1f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val totalCells = ((daysInMonth + firstDayOfWeek - 1) / 7 + 1) * 7
                    var currentDay = 1

                    for (week in 0 until (totalCells / 7)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (dayOfWeek in 1..7) {
                                val cellIndex = week * 7 + dayOfWeek
                                if (cellIndex >= firstDayOfWeek && currentDay <= daysInMonth) {
                                    val d = currentDay
                                    val dateStr = String.format(Locale.US, "%04d-%02d-%02d", displayedYear, displayedMonth + 1, d)
                                    val isCompleted = completedDates.contains(dateStr)
                                    val isSelected = inspectDate == dateStr

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1f)
                                            .padding(2.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isSelected -> FlameGold.copy(alpha = 0.25f)
                                                    isCompleted -> CrimsonSun.copy(alpha = 0.35f)
                                                    else -> DemonSlayerDark
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                when {
                                                    isSelected -> FlameGold
                                                    isCompleted -> CrimsonSun
                                                    else -> DemonSlayerBorder
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { inspectDate = dateStr },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$d",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isCompleted || isSelected) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = if (isCompleted) CrimsonSunGlow else if (isSelected) FlameGold else TextSecondary
                                        )
                                    }
                                    currentDay++
                                } else {
                                    Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Date Inspector & Historical Correction (Section 07.3)
        item {
            val isInspectedDone = completedDates.contains(inspectDate)

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "HISTORICAL DATE AUDIT & CORRECTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Date: $inspectDate",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isInspectedDone) "Status: Completed ✓" else "Status: Not Recorded / Missed",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isInspectedDone) CrimsonSunGlow else TextSecondary
                            )
                        )

                        Button(
                            onClick = {
                                viewModel.toggleHabitCompletion(habit.id, inspectDate, isInspectedDone)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isInspectedDone) DemonSlayerCardElevated else CrimsonSun
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = if (isInspectedDone) "Mark as Undone" else "Mark Completed",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 6. Delete or Archive
        item {
            Button(
                onClick = {
                    viewModel.deleteHabit(habit)
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DemonSlayerCardElevated),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonSunDark),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonSun)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Habit Record", color = CrimsonSun)
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DemonSlayerDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextTertiary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = color)
        }
    }
}

fun calculateLongestStreak(completedDates: Set<String>): Int {
    if (completedDates.isEmpty()) return 0
    val sortedDates = completedDates.sorted()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    var maxStreak = 1
    var currentStreak = 1

    for (i in 1 until sortedDates.size) {
        try {
            val d1 = dateFormat.parse(sortedDates[i - 1])
            val d2 = dateFormat.parse(sortedDates[i])
            if (d1 != null && d2 != null) {
                val diffDays = ((d2.time - d1.time) / (1000 * 60 * 60 * 24)).toInt()
                if (diffDays == 1) {
                    currentStreak++
                    if (currentStreak > maxStreak) maxStreak = currentStreak
                } else if (diffDays > 1) {
                    currentStreak = 1
                }
            }
        } catch (_: Exception) {}
    }
    return maxStreak
}
