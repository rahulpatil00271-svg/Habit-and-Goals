package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.SamuraiScreen
import com.example.ui.SamuraiViewModel
import com.example.ui.components.GrowthNode
import com.example.ui.components.InteractiveGrowthMap
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: SamuraiViewModel,
    onNavigate: (SamuraiScreen, Long?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile = uiState.userProfile
    val habits by viewModel.habitsFlow.collectAsState(initial = emptyList())
    val habitLogs by viewModel.habitLogsFlow.collectAsState(initial = emptyList())
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val goals by viewModel.goalsFlow.collectAsState(initial = emptyList())
    val studySessions by viewModel.studySessionsFlow.collectAsState(initial = emptyList())
    val workouts by viewModel.workoutsFlow.collectAsState(initial = emptyList())
    val journals by viewModel.journalsFlow.collectAsState(initial = emptyList())
    val healthRecords by viewModel.healthRecordsFlow.collectAsState(initial = emptyList())
    val missions by viewModel.missionsFlow.collectAsState(initial = emptyList())

    val todayStr = uiState.todayDateStr
    val char = DemonSlayerDatabase.getCharacter(userProfile?.characterId ?: "tanjiro")

    // Real calculations for today
    val todayLogs = remember(habitLogs, todayStr) {
        habitLogs.filter { it.dateStr == todayStr }
    }
    val completedHabitsCount = remember(todayLogs) {
        todayLogs.count { it.completed }
    }
    val totalHabitsCount = habits.size.coerceAtLeast(1)
    val habitCompletionRate = (completedHabitsCount.toFloat() / totalHabitsCount).coerceIn(0f, 1f)

    val todayTasks = remember(tasks, todayStr) {
        tasks.filter { it.dueDateStr == todayStr || it.dueDateStr.isBlank() }
    }
    val completedTasksCount = todayTasks.count { it.isCompleted }
    val totalTasksCount = todayTasks.size.coerceAtLeast(1)
    val taskCompletionRate = if (todayTasks.isNotEmpty()) (completedTasksCount.toFloat() / totalTasksCount) else 1f

    val todayStudyMinutes = remember(studySessions, todayStr) {
        studySessions.filter { it.dateStr == todayStr }.sumOf { it.durationMinutes }
    }
    val todayWorkoutDone = remember(workouts, todayStr) {
        workouts.any { it.dateStr == todayStr }
    }
    val todayJournalDone = remember(journals, todayStr) {
        journals.any { it.dateStr == todayStr }
    }

    // Overall Daily Completion Rate
    val overallDailyScore = (
        habitCompletionRate * 0.4f +
        taskCompletionRate * 0.3f +
        (if (todayStudyMinutes > 0) 0.15f else 0f) +
        (if (todayWorkoutDone) 0.1f else 0f) +
        (if (todayJournalDone) 0.05f else 0f)
    ) * 100f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_home"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Welcome & Character Header
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    char.themeColor.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "DEMON SLAYER HEADQUARTERS",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = CinzelFontFamily,
                                        letterSpacing = 2.sp
                                    ),
                                    color = FlameGold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Greetings, ${char.name}",
                                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "\"${char.quote}\"",
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                                    color = TextSecondary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(DemonSlayerCardElevated)
                                    .border(2.dp, char.themeColor, CircleShape)
                                    .clickable { onNavigate(SamuraiScreen.SAMURAI_CHAMBER, null) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = char.iconSymbol, fontSize = 26.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Pills Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatMiniBadge(
                                label = "JOURNEY",
                                value = "W${uiState.journeyWeek} • D${uiState.journeyDay}",
                                color = FlameGold,
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniBadge(
                                label = "LEVEL",
                                value = "Lv. ${uiState.journeyWeek}",
                                color = CrimsonSun,
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniBadge(
                                label = "BREATHING",
                                value = userProfile?.activeBreathingPath ?: "Discipline",
                                color = WaterBlue,
                                modifier = Modifier.weight(1f)
                            )
                            StatMiniBadge(
                                label = "TOTAL XP",
                                value = "${userProfile?.totalXp ?: 0}",
                                color = ThunderYellow,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 2. Daily Performance Radial Progress & Weekly Glance
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Circular Progress
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val strokeWidth = 10.dp.toPx()
                            val radius = (size.minDimension - strokeWidth) / 2
                            val center = Offset(size.width / 2, size.height / 2)

                            // Background Track
                            drawCircle(
                                color = DemonSlayerCardElevated,
                                radius = radius,
                                center = center,
                                style = Stroke(strokeWidth)
                            )

                            // Foreground Arc
                            val sweepAngle = (overallDailyScore / 100f) * 360f
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(CrimsonSun, FlameGold, CrimsonSunGlow)
                                ),
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                                style = Stroke(strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${overallDailyScore.toInt()}%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CinzelFontFamily
                                ),
                                color = TextPrimary
                            )
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Performance Breakdown
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PerformanceRow(
                            label = "Habits Mastered",
                            value = "$completedHabitsCount / $totalHabitsCount",
                            progress = habitCompletionRate,
                            color = CrimsonSun
                        )
                        PerformanceRow(
                            label = "Tasks Completed",
                            value = "$completedTasksCount / ${todayTasks.size}",
                            progress = taskCompletionRate,
                            color = FlameGold
                        )
                        PerformanceRow(
                            label = "Study Hours",
                            value = "${todayStudyMinutes / 60}h ${todayStudyMinutes % 60}m",
                            progress = (todayStudyMinutes / 120f).coerceIn(0f, 1f),
                            color = MistCyan
                        )
                        PerformanceRow(
                            label = "Workout & Spirit",
                            value = if (todayWorkoutDone) "Completed ✓" else "Pending",
                            progress = if (todayWorkoutDone) 1f else 0f,
                            color = WindEmerald
                        )
                    }
                }
            }
        }

        // 3. Daily Mission Board: Today's Habits
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚔️ TODAY'S DISCIPLINES",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = CinzelFontFamily,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = FlameGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "($completedHabitsCount/$totalHabitsCount)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                TextButton(onClick = { onNavigate(SamuraiScreen.HABITS, null) }) {
                    Text("View All", color = CrimsonSunGlow, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (habits.isEmpty()) {
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
                        Text(text = "⛩️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active habits scheduled.", color = TextPrimary)
                        Text("Add your first discipline using the Quick Add button.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(habits.take(4)) { habit ->
                val log = todayLogs.find { it.habitId == habit.id }
                val isCompleted = log?.completed == true

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) DemonSlayerCardElevated else DemonSlayerCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCompleted) CrimsonSun.copy(alpha = 0.5f) else DemonSlayerBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(SamuraiScreen.HABIT_DETAIL, habit.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            IconButton(
                                onClick = {
                                    viewModel.toggleHabitCompletion(habit.id, todayStr, isCompleted)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCompleted) CrimsonSun else DemonSlayerDark)
                                    .border(1.5.dp, if (isCompleted) CrimsonSun else DemonSlayerBorder, CircleShape)
                            ) {
                                if (isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = "Done", tint = TextPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = habit.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isCompleted) TextSecondary else TextPrimary
                                    )
                                )
                                Text(
                                    text = "${habit.category} • Target: ${habit.dailyTarget} ${habit.unit}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextTertiary
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Inspect Habit Calendar",
                            tint = TextTertiary
                        )
                    }
                }
            }
        }

        // 4. Tactical Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📜 ACTIVE TASKS & MISSIONS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = CinzelFontFamily,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = FlameGold
                )
                TextButton(onClick = { onNavigate(SamuraiScreen.TASKS, null) }) {
                    Text("Manage", color = CrimsonSunGlow, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (todayTasks.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("All missions cleared for today! Rest or train further.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(todayTasks.take(3)) { task ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { viewModel.toggleTaskCompletion(task) },
                                colors = CheckboxDefaults.colors(checkedColor = FlameGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (task.isCompleted) TextTertiary else TextPrimary
                                    )
                                )
                                if (task.dueDateStr.isNotBlank()) {
                                    Text(
                                        text = "Due: ${task.dueDateStr} • ${task.priority} Priority",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (task.priority == "HIGH") CrimsonSunGlow else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. RPG Missions & Growth Preview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🌟 RPG MISSIONS & BOUNTIES",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = CinzelFontFamily,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = FlameGold
                )
            }
        }

        items(missions.take(3)) { mission ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mission.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FlameGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "+${mission.xpReward} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(color = FlameGold),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = mission.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
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
fun StatMiniBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DemonSlayerCardElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, letterSpacing = 0.5.sp),
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color,
                maxLines = 1
            )
        }
    }
}

@Composable
fun PerformanceRow(
    label: String,
    value: String,
    progress: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(text = value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = DemonSlayerCardElevated,
        )
    }
}
