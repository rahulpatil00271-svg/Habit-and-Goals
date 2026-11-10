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
import com.example.data.model.HabitEntity
import com.example.data.model.HabitLogEntity
import com.example.ui.SamuraiScreen
import com.example.ui.SamuraiViewModel
import com.example.ui.theme.*

@Composable
fun HabitsScreen(
    viewModel: SamuraiViewModel,
    onOpenHabitDetail: (Long) -> Unit,
    onOpenQuickAdd: () -> Unit
) {
    val habits by viewModel.habitsFlow.collectAsState(initial = emptyList())
    val habitLogs by viewModel.habitLogsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = uiState.todayDateStr

    var selectedCategory by remember { mutableStateOf("ALL") }
    var tabIndex by remember { mutableStateOf(0) } // 0: Today, 1: All Disciplines, 2: Completed, 3: Pending

    val categories = listOf("ALL", "Discipline", "Fitness", "Mind", "Academics", "Health", "Creator")

    val filteredHabits = remember(habits, selectedCategory, tabIndex, habitLogs, todayStr) {
        val byCat = if (selectedCategory == "ALL") habits else habits.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        when (tabIndex) {
            0 -> byCat // Today
            1 -> byCat // All
            2 -> byCat.filter { h -> habitLogs.any { it.habitId == h.id && it.dateStr == todayStr && it.completed } }
            3 -> byCat.filter { h -> !habitLogs.any { it.habitId == h.id && it.dateStr == todayStr && it.completed } }
            else -> byCat
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_habits"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                text = "SAMURAI DISCIPLINES",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = CinzelFontFamily,
                                    letterSpacing = 1.sp
                                ),
                                color = FlameGold
                            )
                            Text(
                                text = "Daily Habits & Katas",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = CinzelFontFamily
                                ),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = onOpenQuickAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Habit", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Habit", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tabs
                    TabRow(
                        selectedTabIndex = tabIndex,
                        containerColor = DemonSlayerDark,
                        contentColor = CrimsonSunGlow,
                        divider = {},
                        indicator = {}
                    ) {
                        listOf("Today", "All", "Completed", "Pending").forEachIndexed { index, title ->
                            val isSelected = tabIndex == index
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CrimsonSun.copy(alpha = 0.2f) else Color.Transparent,
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, CrimsonSun) else null,
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clickable { tabIndex = index }
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Category Filter Pills
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FlameGold.copy(alpha = 0.2f) else DemonSlayerCardElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FlameGold else DemonSlayerBorder),
                                modifier = Modifier.clickable { selectedCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) FlameGold else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Habit Cards List
        if (filteredHabits.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "⚔️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No habits found in this view.", color = TextPrimary)
                        Text("Forge a new habit using the '+ New Habit' button.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(filteredHabits) { habit ->
                val logsForHabit = habitLogs.filter { it.habitId == habit.id }
                val isDoneToday = logsForHabit.any { it.dateStr == todayStr && it.completed }

                // Calculate real streak
                val streak = calculateStreak(logsForHabit, todayStr)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDoneToday) DemonSlayerCardElevated else DemonSlayerCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDoneToday) CrimsonSun.copy(alpha = 0.6f) else DemonSlayerBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenHabitDetail(habit.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                IconButton(
                                    onClick = {
                                        viewModel.toggleHabitCompletion(habit.id, todayStr, isDoneToday)
                                    },
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isDoneToday) CrimsonSun else DemonSlayerDark)
                                        .border(2.dp, if (isDoneToday) CrimsonSun else DemonSlayerBorder, CircleShape)
                                ) {
                                    if (isDoneToday) {
                                        Icon(Icons.Default.Check, contentDescription = "Completed", tint = TextPrimary, modifier = Modifier.size(22.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = habit.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDoneToday) TextSecondary else TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "${habit.category} • Target: ${habit.dailyTarget} ${habit.unit}/day",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                }
                            }

                            // Streak Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = FlameGold.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = "🔥", fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$streak Streak",
                                        style = MaterialTheme.typography.labelSmall.copy(color = FlameGold, fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }

                        if (habit.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = habit.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tap card to view 365-day calendar & statistics →",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = CrimsonSunGlow
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

fun calculateStreak(logs: List<HabitLogEntity>, todayStr: String): Int {
    if (logs.isEmpty()) return 0
    val completedDates = logs.filter { it.completed }.map { it.dateStr }.toSet()
    if (completedDates.isEmpty()) return 0

    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
    val cal = java.util.Calendar.getInstance()

    // Start checking from today, or yesterday if today not yet checked
    var streak = 0
    var dateToCheck = todayStr

    if (!completedDates.contains(dateToCheck)) {
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        dateToCheck = dateFormat.format(cal.time)
    }

    while (completedDates.contains(dateToCheck)) {
        streak++
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        dateToCheck = dateFormat.format(cal.time)
    }

    return streak
}
