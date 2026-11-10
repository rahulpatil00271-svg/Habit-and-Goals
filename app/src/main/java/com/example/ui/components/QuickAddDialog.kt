package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.HabitEntity
import com.example.data.model.TaskEntity
import com.example.data.model.GoalEntity
import com.example.data.model.JournalEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.theme.*

enum class QuickAddType {
    HABIT, TASK, GOAL, JOURNAL, STUDY, WORKOUT, HEALTH
}

@Composable
fun QuickAddDialog(
    viewModel: SamuraiViewModel,
    onDismiss: () -> Unit
) {
    var selectedType by remember { mutableStateOf(QuickAddType.HABIT) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f),
            shape = RoundedCornerShape(20.dp),
            color = DemonSlayerCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorderGlow)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "QUICK ADD",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = CinzelFontFamily,
                                letterSpacing = 1.sp
                            ),
                            color = FlameGold
                        )
                        Text(
                            text = "Record training, mission or inner thought",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Selector tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickAddType.values().forEach { type ->
                        val isSelected = selectedType == type
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) CrimsonSun.copy(alpha = 0.2f) else DemonSlayerDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CrimsonSun else DemonSlayerBorder
                            ),
                            modifier = Modifier.clickable { selectedType = type }
                        ) {
                            Text(
                                text = type.name.lowercase().capitalize(),
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) TextPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (selectedType) {
                        QuickAddType.HABIT -> HabitForm(viewModel) { onDismiss() }
                        QuickAddType.TASK -> TaskForm(viewModel) { onDismiss() }
                        QuickAddType.GOAL -> GoalForm(viewModel) { onDismiss() }
                        QuickAddType.JOURNAL -> JournalForm(viewModel) { onDismiss() }
                        QuickAddType.STUDY -> StudyForm(viewModel) { onDismiss() }
                        QuickAddType.WORKOUT -> WorkoutForm(viewModel) { onDismiss() }
                        QuickAddType.HEALTH -> HealthForm(viewModel) { onDismiss() }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitForm(viewModel: SamuraiViewModel, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Discipline") }
    var dailyTarget by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("times") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit Name") },
            placeholder = { Text("e.g. 50 Katana Swings, 1h Deep Code") },
            modifier = Modifier.fillMaxWidth().testTag("input_habit_name"),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description / Purpose") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = dailyTarget,
                onValueChange = { dailyTarget = it },
                label = { Text("Daily Target") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
        }

        // Category Selection
        Text("Category:", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Discipline", "Fitness", "Mind", "Academics", "Health", "Creator").forEach { cat ->
                val isSelected = category == cat
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) FlameGold.copy(alpha = 0.2f) else DemonSlayerDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FlameGold else DemonSlayerBorder),
                    modifier = Modifier.clickable { category = cat }
                ) {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) FlameGold else TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (name.isNotBlank()) {
                    viewModel.saveHabit(
                        HabitEntity(
                            name = name.trim(),
                            description = description.trim(),
                            category = category,
                            dailyTarget = dailyTarget.toFloatOrNull() ?: 1f,
                            unit = unit.trim()
                        )
                    )
                    onDone()
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_save_habit"),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun)
        ) {
            Text("Begin Habit Discipline", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TaskForm(viewModel: SamuraiViewModel, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("HIGH") }
    var dueDate by remember { mutableStateOf(viewModel.repository.getTodayDateStr()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            placeholder = { Text("e.g. Finish Algorithm Set, Fix Deployment") },
            modifier = Modifier.fillMaxWidth().testTag("input_task_title"),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Details & Objectives") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = dueDate,
            onValueChange = { dueDate = it },
            label = { Text("Due Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )

        Text("Priority:", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("HIGH" to CrimsonSun, "MEDIUM" to FlameGold, "LOW" to WaterBlue).forEach { (p, col) ->
                val isSelected = priority == p
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) col.copy(alpha = 0.2f) else DemonSlayerDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) col else DemonSlayerBorder),
                    modifier = Modifier.clickable { priority = p }
                ) {
                    Text(
                        text = p,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) col else TextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.saveTask(
                        TaskEntity(
                            title = title.trim(),
                            description = description.trim(),
                            priority = priority,
                            dueDateStr = dueDate.trim()
                        )
                    )
                    onDone()
                }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_save_task"),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun)
        ) {
            Text("Issue Task Mission", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GoalForm(viewModel: SamuraiViewModel, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Fitness") }
    var targetValue by remember { mutableStateOf("100") }
    var unit by remember { mutableStateOf("%") }
    var deadline by remember { mutableStateOf(viewModel.repository.getDateOffsetStr(30)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Goal Title") },
            placeholder = { Text("e.g. Master Muscle-up, 100h Coding") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description & Milestone Vision") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = targetValue,
                onValueChange = { targetValue = it },
                label = { Text("Target") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
            OutlinedTextField(
                value = unit,
                onValueChange = { unit = it },
                label = { Text("Unit") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
        }
        OutlinedTextField(
            value = deadline,
            onValueChange = { deadline = it },
            label = { Text("Target Deadline (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.saveGoal(
                        GoalEntity(
                            title = title.trim(),
                            description = description.trim(),
                            category = category,
                            targetValue = targetValue.toFloatOrNull() ?: 100f,
                            unit = unit.trim(),
                            startDateStr = viewModel.repository.getTodayDateStr(),
                            deadlineStr = deadline.trim()
                        )
                    )
                    onDone()
                }
            },
            enabled = title.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = FlameGold)
        ) {
            Text("Inscribe Grand Goal", fontWeight = FontWeight.Bold, color = DemonSlayerBlack)
        }
    }
}

@Composable
fun JournalForm(viewModel: SamuraiViewModel, onDone: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("Focused") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Entry Title") },
            placeholder = { Text("e.g. Total Concentration in the Storm") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Journal Reflections & Observations") },
            modifier = Modifier.fillMaxWidth().height(140.dp),
            colors = customTextFieldColors(),
            maxLines = 8
        )

        Text("Current Mental State (Mood):", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Peaceful", "Motivated", "Focused", "Challenged", "Victorious", "Contemplative").forEach { m ->
                val isSelected = mood == m
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) WaterBlue.copy(alpha = 0.2f) else DemonSlayerDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) WaterBlue else DemonSlayerBorder),
                    modifier = Modifier.clickable { mood = m }
                ) {
                    Text(
                        text = m,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) WaterBlueGlow else TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (title.isNotBlank() || content.isNotBlank()) {
                    viewModel.saveJournal(
                        JournalEntity(
                            title = if (title.isBlank()) "Daily Scroll" else title.trim(),
                            content = content.trim(),
                            dateStr = viewModel.repository.getTodayDateStr(),
                            mood = mood
                        )
                    )
                    onDone()
                }
            },
            enabled = title.isNotBlank() || content.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WaterBlue)
        ) {
            Text("Inscribe in Scroll", fontWeight = FontWeight.Bold, color = DemonSlayerBlack)
        }
    }
}

@Composable
fun StudyForm(viewModel: SamuraiViewModel, onDone: () -> Unit) {
    var subject by remember { mutableStateOf("Computer Science & Architecture") }
    var topic by remember { mutableStateOf("") }
    var durationMins by remember { mutableStateOf("60") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Subject") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Topic Studied") },
            placeholder = { Text("e.g. Concurrency, Tree Algorithms") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = durationMins,
            onValueChange = { durationMins = it },
            label = { Text("Duration (Minutes)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes / Insights") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )

        Button(
            onClick = {
                val mins = durationMins.toIntOrNull() ?: 60
                viewModel.logStudySession(subject.trim(), topic.trim(), mins, notes.trim(), "SELF_STUDY")
                onDone()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MistCyan)
        ) {
            Text("Record Knowledge Session", fontWeight = FontWeight.Bold, color = DemonSlayerBlack)
        }
    }
}

@Composable
fun WorkoutForm(viewModel: SamuraiViewModel, onDone: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Calisthenics") }
    var durationMins by remember { mutableStateOf("45") }
    var exercises by remember { mutableStateOf("") }
    var isPr by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Workout Name") },
            placeholder = { Text("e.g. Upper Body Explosive, Leg Day") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = durationMins,
            onValueChange = { durationMins = it },
            label = { Text("Duration (Minutes)") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )
        OutlinedTextField(
            value = exercises,
            onValueChange = { exercises = it },
            label = { Text("Exercises, Sets & Reps") },
            placeholder = { Text("Pull-ups 4x10, Dips 4x15, Push-ups 3x25") },
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors()
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isPr,
                onCheckedChange = { isPr = it },
                colors = CheckboxDefaults.colors(checkedColor = FlameGold)
            )
            Text("New Personal Record (PR) Achieved!", color = FlameGold, style = MaterialTheme.typography.bodyMedium)
        }

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    viewModel.logWorkout(
                        name = name.trim(),
                        category = category,
                        durationMinutes = durationMins.toIntOrNull() ?: 45,
                        exercisesSummary = exercises.trim(),
                        notes = "",
                        isPr = isPr
                    )
                    onDone()
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun)
        ) {
            Text("Log Dojo Workout", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun HealthForm(viewModel: SamuraiViewModel, onDone: () -> Unit) {
    var sleepHours by remember { mutableStateOf("7.5") }
    var sleepQuality by remember { mutableStateOf(4) }
    var hydrationMl by remember { mutableStateOf("2500") }
    var recoveryScore by remember { mutableStateOf("85") }
    var energyLevel by remember { mutableStateOf("8") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = sleepHours,
                onValueChange = { sleepHours = it },
                label = { Text("Sleep (Hours)") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
            OutlinedTextField(
                value = hydrationMl,
                onValueChange = { hydrationMl = it },
                label = { Text("Water (ml)") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = recoveryScore,
                onValueChange = { recoveryScore = it },
                label = { Text("Recovery Score (0-100)") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
            OutlinedTextField(
                value = energyLevel,
                onValueChange = { energyLevel = it },
                label = { Text("Energy (1-10)") },
                modifier = Modifier.weight(1f),
                colors = customTextFieldColors()
            )
        }

        Text("Sleep Quality: $sleepQuality Stars", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..5).forEach { star ->
                Text(
                    text = if (star <= sleepQuality) "★" else "☆",
                    fontSize = 24.sp,
                    color = FlameGold,
                    modifier = Modifier.clickable { sleepQuality = star }
                )
            }
        }

        Button(
            onClick = {
                viewModel.logHealth(
                    sleepHours = sleepHours.toFloatOrNull() ?: 7.5f,
                    sleepQuality = sleepQuality,
                    hydrationMl = hydrationMl.toIntOrNull() ?: 2500,
                    recoveryScore = recoveryScore.toIntOrNull() ?: 85,
                    energyLevel = energyLevel.toIntOrNull() ?: 8,
                    notes = ""
                )
                onDone()
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WindEmerald)
        ) {
            Text("Record Vitality & Health", fontWeight = FontWeight.Bold, color = DemonSlayerBlack)
        }
    }
}

@Composable
fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CrimsonSun,
    unfocusedBorderColor = DemonSlayerBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = CrimsonSunGlow,
    unfocusedLabelColor = TextSecondary,
    cursorColor = CrimsonSun
)
