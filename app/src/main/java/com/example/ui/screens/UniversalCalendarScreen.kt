package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.*
import com.example.ui.SamuraiScreen
import com.example.ui.SamuraiViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class CalendarViewMode {
    MONTH, WEEK, AGENDA, UPCOMING
}

@Composable
fun UniversalCalendarScreen(
    viewModel: SamuraiViewModel,
    onNavigate: (SamuraiScreen, Long?) -> Unit,
    onOpenQuickAdd: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val habits by viewModel.habitsFlow.collectAsState(initial = emptyList())
    val habitLogs by viewModel.habitLogsFlow.collectAsState(initial = emptyList())
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val goals by viewModel.goalsFlow.collectAsState(initial = emptyList())
    val studySessions by viewModel.studySessionsFlow.collectAsState(initial = emptyList())
    val exams by viewModel.examsFlow.collectAsState(initial = emptyList())
    val workouts by viewModel.workoutsFlow.collectAsState(initial = emptyList())
    val journals by viewModel.journalsFlow.collectAsState(initial = emptyList())
    val reflectionAnswers by viewModel.reflectionAnswersFlow.collectAsState(initial = emptyList())
    val healthRecords by viewModel.healthRecordsFlow.collectAsState(initial = emptyList())

    val todayStr = uiState.todayDateStr
    var selectedDate by remember { mutableStateOf(uiState.selectedCalendarDate.ifBlank { todayStr }) }
    var viewMode by remember { mutableStateOf(CalendarViewMode.MONTH) }
    var activeCategoryFilter by remember { mutableStateOf("ALL") }

    // Month navigation calendar
    val calendar = remember { Calendar.getInstance() }
    var displayedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var displayedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) } // 0-based

    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.US) }
    val displayCalendar = remember(displayedYear, displayedMonth) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, displayedYear)
        c.set(Calendar.MONTH, displayedMonth)
        c.set(Calendar.DAY_OF_MONTH, 1)
        c
    }

    val daysInMonth = displayCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = displayCalendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, etc.

    // Filters list
    val filters = listOf("ALL", "HABITS", "TASKS", "GOALS", "ACADEMICS", "FITNESS", "HEALTH", "JOURNAL")

    // Get all records for selected date
    val selectedHabitLogs = remember(habitLogs, selectedDate) { habitLogs.filter { it.dateStr == selectedDate } }
    val selectedTasks = remember(tasks, selectedDate) { tasks.filter { it.dueDateStr == selectedDate } }
    val selectedGoals = remember(goals, selectedDate) { goals.filter { it.deadlineStr == selectedDate } }
    val selectedStudy = remember(studySessions, selectedDate) { studySessions.filter { it.dateStr == selectedDate } }
    val selectedExams = remember(exams, selectedDate) { exams.filter { it.dateStr == selectedDate } }
    val selectedWorkouts = remember(workouts, selectedDate) { workouts.filter { it.dateStr == selectedDate } }
    val selectedJournals = remember(journals, selectedDate) { journals.filter { it.dateStr == selectedDate } }
    val selectedReflections = remember(reflectionAnswers, selectedDate) { reflectionAnswers.filter { it.dateStr == selectedDate } }
    val selectedHealth = remember(healthRecords, selectedDate) { healthRecords.find { it.dateStr == selectedDate } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_calendar"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Calendar Header & Month Navigation
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
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
                                text = "UNIVERSAL LIFE CALENDAR",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = CinzelFontFamily,
                                    letterSpacing = 1.sp
                                ),
                                color = FlameGold
                            )
                            Text(
                                text = monthFormat.format(displayCalendar.time),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CinzelFontFamily
                                ),
                                color = TextPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    if (displayedMonth == 0) {
                                        displayedMonth = 11
                                        displayedYear--
                                    } else {
                                        displayedMonth--
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = TextPrimary)
                            }
                            IconButton(
                                onClick = {
                                    val now = Calendar.getInstance()
                                    displayedYear = now.get(Calendar.YEAR)
                                    displayedMonth = now.get(Calendar.MONTH)
                                    selectedDate = todayStr
                                }
                            ) {
                                Icon(Icons.Default.Today, contentDescription = "Jump to Today", tint = FlameGold)
                            }
                            IconButton(
                                onClick = {
                                    if (displayedMonth == 11) {
                                        displayedMonth = 0
                                        displayedYear++
                                    } else {
                                        displayedMonth++
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // View Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalendarViewMode.values().forEach { mode ->
                            val isSelected = viewMode == mode
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CrimsonSun.copy(alpha = 0.25f) else DemonSlayerDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CrimsonSun else DemonSlayerBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewMode = mode }
                            ) {
                                Text(
                                    text = mode.name.lowercase().capitalize(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) CrimsonSunGlow else TextSecondary,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Filters Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filters) { f ->
                            val isSelected = activeCategoryFilter == f
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FlameGold.copy(alpha = 0.2f) else DemonSlayerCardElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FlameGold else DemonSlayerBorder),
                                modifier = Modifier.clickable { activeCategoryFilter = f }
                            ) {
                                Text(
                                    text = f,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isSelected) FlameGold else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Month Calendar Grid (when viewMode == MONTH)
        if (viewMode == CalendarViewMode.MONTH) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Days of week header
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { dayName ->
                                Text(
                                    text = dayName,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (dayName == "Sun" || dayName == "Sat") CrimsonSunGlow else TextSecondary,
                                    modifier = Modifier.weight(1f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Grid of days
                        val totalCells = ((daysInMonth + firstDayOfWeek - 1) / 7 + 1) * 7
                        var currentDay = 1

                        for (week in 0 until (totalCells / 7)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (dayOfWeek in 1..7) {
                                    val cellIndex = week * 7 + dayOfWeek
                                    if (cellIndex >= firstDayOfWeek && currentDay <= daysInMonth) {
                                        val dayNumber = currentDay
                                        val dateStr = String.format(Locale.US, "%04d-%02d-%02d", displayedYear, displayedMonth + 1, dayNumber)
                                        val isSelected = selectedDate == dateStr
                                        val isToday = todayStr == dateStr

                                        // Check indicators for this date
                                        val hasHabits = habitLogs.any { it.dateStr == dateStr && it.completed }
                                        val hasTasks = tasks.any { it.dueDateStr == dateStr }
                                        val hasStudy = studySessions.any { it.dateStr == dateStr }
                                        val hasWorkout = workouts.any { it.dateStr == dateStr }
                                        val hasJournal = journals.any { it.dateStr == dateStr }

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .padding(2.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    when {
                                                        isSelected -> CrimsonSun.copy(alpha = 0.35f)
                                                        isToday -> DemonSlayerCardElevated
                                                        else -> Color.Transparent
                                                    }
                                                )
                                                .border(
                                                    1.dp,
                                                    when {
                                                        isSelected -> CrimsonSun
                                                        isToday -> FlameGold.copy(alpha = 0.5f)
                                                        else -> Color.Transparent
                                                    },
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedDate = dateStr },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "$dayNumber",
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                                    ),
                                                    color = when {
                                                        isSelected -> TextPrimary
                                                        isToday -> FlameGold
                                                        else -> TextSecondary
                                                    }
                                                )
                                                // Activity badge dots
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                                    modifier = Modifier.padding(top = 2.dp)
                                                ) {
                                                    if (hasHabits) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(CrimsonSun))
                                                    if (hasTasks) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(FlameGold))
                                                    if (hasStudy) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(MistCyan))
                                                    if (hasWorkout) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(WindEmerald))
                                                    if (hasJournal) Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(WaterBlue))
                                                }
                                            }
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
        }

        // 3. Daily Activity Panel (The Universal Record for the selected date)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, FlameGold.copy(alpha = 0.4f)),
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
                                text = "DATE DOSSIER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = CinzelFontFamily,
                                    letterSpacing = 1.sp
                                ),
                                color = FlameGold
                            )
                            Text(
                                text = selectedDate,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontFamily = CinzelFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = onOpenQuickAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add record", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Record", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Detail sections for selected date
                    // A. Habits on this date
                    CalendarSectionHeader("⚔️ HABITS & DISCIPLINES", selectedHabitLogs.size)
                    if (selectedHabitLogs.isEmpty()) {
                        Text("No habit activity recorded on this date.", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    } else {
                        selectedHabitLogs.forEach { log ->
                            val habit = habits.find { it.id == log.habitId }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = habit?.name ?: "Habit #${log.habitId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (log.completed) TextPrimary else TextTertiary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (log.completed) CrimsonSun.copy(alpha = 0.2f) else DemonSlayerDark
                                ) {
                                    Text(
                                        text = if (log.completed) "Completed ✓" else "Pending",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (log.completed) CrimsonSunGlow else TextSecondary
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = DemonSlayerBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // B. Tasks due on this date
                    CalendarSectionHeader("📜 TASKS & DEADLINES", selectedTasks.size)
                    if (selectedTasks.isEmpty()) {
                        Text("No task deadlines on this date.", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    } else {
                        selectedTasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (task.isCompleted) TextTertiary else TextPrimary
                                )
                                Text(
                                    text = if (task.isCompleted) "Cleared" else "${task.priority} Priority",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (task.isCompleted) WindEmerald else FlameGold
                                    )
                                )
                            }
                        }
                    }

                    Divider(color = DemonSlayerBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // C. Study Sessions
                    CalendarSectionHeader("📚 KNOWLEDGE SESSIONS", selectedStudy.size)
                    if (selectedStudy.isEmpty()) {
                        Text("No study sessions logged on this date.", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    } else {
                        selectedStudy.forEach { session ->
                            Text(
                                text = "• ${session.subjectName} (${session.durationMinutes} mins) - ${session.topic}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MistCyan
                            )
                        }
                    }

                    Divider(color = DemonSlayerBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // D. Workouts
                    CalendarSectionHeader("🏋️ PHYSICAL TRAINING", selectedWorkouts.size)
                    if (selectedWorkouts.isEmpty()) {
                        Text("No workouts recorded on this date.", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    } else {
                        selectedWorkouts.forEach { w ->
                            Text(
                                text = "• ${w.name} (${w.category}, ${w.durationMinutes} mins) ${if (w.isPr) "★ PR!" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = WindEmerald
                            )
                        }
                    }

                    Divider(color = DemonSlayerBorder, modifier = Modifier.padding(vertical = 10.dp))

                    // E. Journal & Reflection
                    CalendarSectionHeader("🌸 JOURNAL SCROLLS & REFLECTIONS", selectedJournals.size + selectedReflections.size)
                    if (selectedJournals.isEmpty() && selectedReflections.isEmpty()) {
                        Text("No journal scrolls or reflections recorded on this date.", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                    } else {
                        selectedJournals.forEach { j ->
                            Text(
                                text = "• Journal: \"${j.title}\" (${j.mood})",
                                style = MaterialTheme.typography.bodySmall,
                                color = WaterBlue
                            )
                        }
                        if (selectedReflections.isNotEmpty()) {
                            Text(
                                text = "• Daily Reflection: ${selectedReflections.size} questions answered",
                                style = MaterialTheme.typography.bodySmall,
                                color = InsectPurple
                            )
                        }
                    }

                    selectedHealth?.let { h ->
                        Divider(color = DemonSlayerBorder, modifier = Modifier.padding(vertical = 10.dp))
                        CalendarSectionHeader("🌿 VITALITY & HEALTH", 1)
                        Text(
                            text = "Sleep: ${h.sleepHours}h (${h.sleepQuality}★) • Hydration: ${h.hydrationMl}ml • Recovery: ${h.recoveryScore}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = WindEmerald
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CalendarSectionHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
            color = FlameGold
        )
        if (count > 0) {
            Text(
                text = "$count recorded",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}
