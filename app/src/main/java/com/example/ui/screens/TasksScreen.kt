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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.components.StatBox
import com.example.ui.theme.*

@Composable
fun TasksScreen(
    viewModel: SamuraiViewModel,
    onOpenQuickAdd: () -> Unit
) {
    val tasks by viewModel.tasksFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = uiState.todayDateStr

    var selectedFilter by remember { mutableStateOf("TODAY") } // TODAY, UPCOMING, THIS_WEEK, COMPLETED, OVERDUE, ALL

    val completedCount = remember(tasks) { tasks.count { it.isCompleted } }
    val pendingCount = remember(tasks) { tasks.count { !it.isCompleted } }
    val overdueCount = remember(tasks, todayStr) {
        tasks.count { !it.isCompleted && it.dueDateStr.isNotBlank() && it.dueDateStr < todayStr }
    }

    val filteredTasks = remember(tasks, selectedFilter, todayStr) {
        when (selectedFilter) {
            "TODAY" -> tasks.filter { it.dueDateStr == todayStr || it.dueDateStr.isBlank() }
            "UPCOMING" -> tasks.filter { !it.isCompleted && it.dueDateStr > todayStr }
            "COMPLETED" -> tasks.filter { it.isCompleted }
            "OVERDUE" -> tasks.filter { !it.isCompleted && it.dueDateStr.isNotBlank() && it.dueDateStr < todayStr }
            else -> tasks // ALL
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_tasks"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Analytics Summary
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
                                text = "TACTICAL MISSIONS",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                                color = FlameGold
                            )
                            Text(
                                text = "Tasks & Operations",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = onOpenQuickAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "New Task", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Task", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Task Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox("Pending", "$pendingCount", FlameGold, Modifier.weight(1f))
                        StatBox("Completed", "$completedCount", WindEmerald, Modifier.weight(1f))
                        StatBox("Overdue", "$overdueCount", if (overdueCount > 0) CrimsonSunGlow else TextSecondary, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Filter Tabs
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(listOf("TODAY", "UPCOMING", "OVERDUE", "COMPLETED", "ALL")) { f ->
                            val isSelected = selectedFilter == f
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) CrimsonSun.copy(alpha = 0.25f) else DemonSlayerDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CrimsonSun else DemonSlayerBorder),
                                modifier = Modifier.clickable { selectedFilter = f }
                            ) {
                                Text(
                                    text = f.replace("_", " "),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Task Items List
        if (filteredTasks.isEmpty()) {
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
                        Text(text = "📜", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No missions matching this filter.", color = TextPrimary)
                        Text("Schedule a task using the '+ New Task' button.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        } else {
            items(filteredTasks) { task ->
                val isOverdue = !task.isCompleted && task.dueDateStr.isNotBlank() && task.dueDateStr < todayStr

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (task.isCompleted) DemonSlayerCardElevated else DemonSlayerCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            task.isCompleted -> DemonSlayerBorder
                            isOverdue -> CrimsonSun
                            task.priority == "HIGH" -> FlameGold.copy(alpha = 0.5f)
                            else -> DemonSlayerBorder
                        }
                    ),
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
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (task.isCompleted) TextTertiary else TextPrimary
                                    )
                                )
                                if (task.description.isNotBlank()) {
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary,
                                        maxLines = 1
                                    )
                                }
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (task.dueDateStr.isNotBlank()) {
                                        Text(
                                            text = if (isOverdue) "⚠️ Overdue (${task.dueDateStr})" else "Due: ${task.dueDateStr}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isOverdue) CrimsonSunGlow else TextSecondary
                                            )
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (task.priority) {
                                            "HIGH" -> CrimsonSun.copy(alpha = 0.2f)
                                            "MEDIUM" -> FlameGold.copy(alpha = 0.2f)
                                            else -> WaterBlue.copy(alpha = 0.2f)
                                        }
                                    ) {
                                        Text(
                                            text = task.priority,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = when (task.priority) {
                                                    "HIGH" -> CrimsonSunGlow
                                                    "MEDIUM" -> FlameGold
                                                    else -> WaterBlueGlow
                                                }
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.deleteTask(task) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = TextTertiary)
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
