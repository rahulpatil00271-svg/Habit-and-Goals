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
import com.example.data.model.SkillProgressEntity
import com.example.data.model.WorkoutEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.components.StatBox
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.*

@Composable
fun FitnessScreen(
    viewModel: SamuraiViewModel,
    onOpenQuickAdd: () -> Unit
) {
    val workouts by viewModel.workoutsFlow.collectAsState(initial = emptyList())
    val skills by viewModel.skillsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = uiState.todayDateStr

    var selectedSkillForEdit by remember { mutableStateOf<SkillProgressEntity?>(null) }
    var skillNotesInput by remember { mutableStateOf("") }
    var skillStageInput by remember { mutableStateOf(1) }

    val totalWorkouts = workouts.size
    val totalPrs = workouts.count { it.isPr }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_fitness"),
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
                                text = "FITNESS DOJO",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                                color = FlameGold
                            )
                            Text(
                                text = "Physical Training & Calisthenics",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = onOpenQuickAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Log Workout", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Workout", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox("Total Sessions", "$totalWorkouts Workouts", FlameGold, Modifier.weight(1f))
                        StatBox("Personal Records", "$totalPrs PRs", CrimsonSun, Modifier.weight(1f))
                        StatBox("Mastery Skills", "${skills.size} Tracked", WaterBlue, Modifier.weight(1f))
                    }
                }
            }
        }

        // Calisthenics Skill Progression (Handstand, Muscle-up, Pull-ups, Push-ups, L-Sit, Pistol Squat)
        item {
            Text(
                text = "🥋 CALISTHENICS & MASTERY SKILLS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = CinzelFontFamily,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = FlameGold
            )
        }

        items(skills) { skill ->
            val progressPercent = (skill.currentStage.toFloat() / skill.maxStage).coerceIn(0f, 1f)

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
                                text = skill.skillName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = skill.stageTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = FlameGold
                            )
                        }

                        Button(
                            onClick = {
                                selectedSkillForEdit = skill
                                skillStageInput = skill.currentStage
                                skillNotesInput = skill.notes
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FlameGold.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Upgrade Stage", style = MaterialTheme.typography.labelSmall.copy(color = FlameGold, fontWeight = FontWeight.Bold))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mastery Level: Stage ${skill.currentStage} of ${skill.maxStage}", style = MaterialTheme.typography.bodySmall, color = TextTertiary)
                        Text("${(progressPercent * 100).toInt()}%", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = CrimsonSun,
                        trackColor = DemonSlayerDark
                    )

                    if (skill.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Form Notes: ${skill.notes}",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Recent Workouts Logged
        item {
            Text(
                text = "📜 DOJO WORKOUT LEDGER",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = CinzelFontFamily,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = FlameGold
            )
        }

        if (workouts.isEmpty()) {
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
                        Text("No training logged yet.", color = TextSecondary)
                    }
                }
            }
        } else {
            items(workouts.take(6)) { w ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (w.isPr) FlameGold else DemonSlayerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = w.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                if (w.isPr) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = FlameGold.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "★ NEW PR",
                                            style = MaterialTheme.typography.labelSmall.copy(color = FlameGold, fontSize = 9.sp),
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${w.category} • ${w.durationMinutes} mins • Date: ${w.dateStr}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            if (w.exercisesSummary.isNotBlank()) {
                                Text(
                                    text = w.exercisesSummary,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Modal to upgrade skill stage
    selectedSkillForEdit?.let { skill ->
        AlertDialog(
            onDismissRequest = { selectedSkillForEdit = null },
            title = {
                Text(
                    text = "Upgrade Skill: ${skill.skillName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = FlameGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Stage (1 to 5):", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        (1..5).forEach { stage ->
                            val isSelected = skillStageInput == stage
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CrimsonSun else DemonSlayerDark,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { skillStageInput = stage }
                            ) {
                                Text(
                                    text = "S$stage",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = skillNotesInput,
                        onValueChange = { skillNotesInput = it },
                        label = { Text("Form Mastery & Cues") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newTitle = "Stage $skillStageInput: Advanced Progression"
                        viewModel.updateSkillStage(skill, skillStageInput, newTitle, skillNotesInput.trim())
                        selectedSkillForEdit = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun)
                ) {
                    Text("Confirm Upgrade", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedSkillForEdit = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DemonSlayerCard
        )
    }
}
