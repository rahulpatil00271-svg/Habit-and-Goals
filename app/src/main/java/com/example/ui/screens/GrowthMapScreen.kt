package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SamuraiScreen
import com.example.ui.SamuraiViewModel
import com.example.ui.components.GrowthNode
import com.example.ui.components.InteractiveGrowthMap
import com.example.ui.theme.*

@Composable
fun GrowthMapScreen(
    viewModel: SamuraiViewModel,
    onNavigate: (SamuraiScreen, Long?) -> Unit
) {
    val habits by viewModel.habitsFlow.collectAsState(initial = emptyList())
    val habitLogs by viewModel.habitLogsFlow.collectAsState(initial = emptyList())
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val studySessions by viewModel.studySessionsFlow.collectAsState(initial = emptyList())
    val workouts by viewModel.workoutsFlow.collectAsState(initial = emptyList())
    val journals by viewModel.journalsFlow.collectAsState(initial = emptyList())
    val healthRecords by viewModel.healthRecordsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()

    // Real computations for growth nodes
    val habitPercent = remember(habits, habitLogs) {
        if (habits.isNotEmpty()) {
            val completed = habitLogs.count { it.completed }
            ((completed.toFloat() / (habits.size * 7).coerceAtLeast(1)) * 100f).toInt().coerceIn(15, 100)
        } else 20
    }
    val taskPercent = remember(tasks) {
        if (tasks.isNotEmpty()) ((tasks.count { it.isCompleted }.toFloat() / tasks.size) * 100f).toInt().coerceIn(10, 100) else 50
    }
    val fitnessPercent = remember(workouts) {
        ((workouts.size / 10f) * 100f).toInt().coerceIn(15, 100)
    }
    val academicPercent = remember(studySessions) {
        ((studySessions.sumOf { it.durationMinutes } / 600f) * 100f).toInt().coerceIn(10, 100)
    }
    val reflectionPercent = remember(journals) {
        ((journals.size / 8f) * 100f).toInt().coerceIn(10, 100)
    }
    val healthPercent = remember(healthRecords) {
        if (healthRecords.isNotEmpty()) healthRecords.map { it.recoveryScore }.average().toInt() else 85
    }

    val nodes = remember(habitPercent, taskPercent, fitnessPercent, academicPercent, reflectionPercent, healthPercent) {
        listOf(
            GrowthNode("Discipline Dojo", "Habit Consistency & Katas", SamuraiScreen.HABITS, habitPercent, "⚔️", CrimsonSun, "Rank 1", 0.5f, 0.45f),
            GrowthNode("Mission Citadel", "Tactical Operations & Deadlines", SamuraiScreen.TASKS, taskPercent, "📜", FlameGold, "Rank 1", 0.22f, 0.25f),
            GrowthNode("Knowledge Temple", "Curriculum & Deep Focus", SamuraiScreen.ACADEMICS, academicPercent, "📚", MistCyan, "Rank 1", 0.78f, 0.25f),
            GrowthNode("Fitness Arena", "Calisthenics & Strength", SamuraiScreen.FITNESS, fitnessPercent, "🏋️", WindEmerald, "Rank 1", 0.22f, 0.72f),
            GrowthNode("Reflection Garden", "Journal & Mindfulness", SamuraiScreen.REFLECTION, reflectionPercent, "🌸", WaterBlue, "Rank 1", 0.78f, 0.72f),
            GrowthNode("Health Sanctuary", "Sleep, Water & Vitality", SamuraiScreen.HEALTH, healthPercent, "🌿", InsectPurple, "Rank 1", 0.5f, 0.88f)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_growth_map"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "GROWTH CONSTELLATION MAP",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )
                    Text(
                        text = "Visual Dimension Mastery Map",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap on any glowing celestial shrine below to inspect its development level and jump directly to its training section.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            InteractiveGrowthMap(
                nodes = nodes,
                onSelectNode = { screen -> onNavigate(screen, null) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
