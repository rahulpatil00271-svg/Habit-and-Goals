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
import com.example.data.model.GoalEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.components.StatBox
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.*

@Composable
fun GoalsScreen(
    viewModel: SamuraiViewModel,
    onOpenQuickAdd: () -> Unit
) {
    val goals by viewModel.goalsFlow.collectAsState(initial = emptyList())
    var selectedGoalForProgress by remember { mutableStateOf<GoalEntity?>(null) }
    var progressDeltaInput by remember { mutableStateOf("10") }

    val activeGoals = remember(goals) { goals.filter { it.status == "ACTIVE" } }
    val completedGoals = remember(goals) { goals.filter { it.status == "COMPLETED" } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_goals"),
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
                                text = "GOAL SHRINE",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                                color = FlameGold
                            )
                            Text(
                                text = "Grand Ambitions & Milestones",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = onOpenQuickAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = FlameGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Goal", tint = DemonSlayerBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Goal", fontWeight = FontWeight.Bold, color = DemonSlayerBlack)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox("Active Objectives", "${activeGoals.size}", FlameGold, Modifier.weight(1f))
                        StatBox("Mastered Goals", "${completedGoals.size}", WindEmerald, Modifier.weight(1f))
                        StatBox("Total Ambitions", "${goals.size}", WaterBlue, Modifier.weight(1f))
                    }
                }
            }
        }

        if (goals.isEmpty()) {
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
                        Text(text = "🏮", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No grand goals inscribed yet.", color = TextPrimary)
                        Text("Inscribe a major goal using the '+ New Goal' button.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(goals) { goal ->
                val progressPercent = if (goal.targetValue > 0f) {
                    ((goal.currentValue / goal.targetValue) * 100f).coerceIn(0f, 100f)
                } else 0f
                val isDone = goal.status == "COMPLETED" || goal.currentValue >= goal.targetValue

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDone) DemonSlayerCardElevated else DemonSlayerCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDone) WindEmerald.copy(alpha = 0.6f) else DemonSlayerBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = goal.title,
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = if (isDone) WindEmerald else TextPrimary
                                    )
                                    if (isDone) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("✓", color = WindEmerald, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    text = "${goal.category} • ${goal.type} Goal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }

                            // Update Progress Button
                            Button(
                                onClick = {
                                    selectedGoalForProgress = goal
                                    progressDeltaInput = "10"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FlameGold.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("+ Progress", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = FlameGold))
                            }
                        }

                        if (goal.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = goal.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Bar & Numbers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${goal.currentValue} / ${goal.targetValue} ${goal.unit}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "${progressPercent.toInt()}%",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isDone) WindEmerald else FlameGold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isDone) WindEmerald else FlameGold,
                            trackColor = DemonSlayerDark
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (goal.deadlineStr.isNotBlank()) {
                                Text(
                                    text = "Target Deadline: ${goal.deadlineStr}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteGoal(goal) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(16.dp))
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

    // Modal to record progress update
    selectedGoalForProgress?.let { goal ->
        AlertDialog(
            onDismissRequest = { selectedGoalForProgress = null },
            title = {
                Text(
                    text = "Record Progress: ${goal.title}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = FlameGold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Current: ${goal.currentValue} / ${goal.targetValue} ${goal.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = progressDeltaInput,
                        onValueChange = { progressDeltaInput = it },
                        label = { Text("Progress Delta (+ Amount)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = customTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val delta = progressDeltaInput.toFloatOrNull() ?: 10f
                        viewModel.updateGoalProgress(goal, delta)
                        selectedGoalForProgress = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameGold)
                ) {
                    Text("Apply Progress", color = DemonSlayerBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedGoalForProgress = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = DemonSlayerCard
        )
    }
}
