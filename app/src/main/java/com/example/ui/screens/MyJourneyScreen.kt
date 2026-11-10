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
import com.example.data.model.JourneyLogEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyJourneyScreen(
    viewModel: SamuraiViewModel
) {
    val journeyLogs by viewModel.journeyLogsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val categories = listOf("ALL", "Habits", "Tasks", "Goals", "Academics", "Fitness", "Mind", "Health", "Evolution")

    val filteredLogs = remember(journeyLogs, selectedCategoryFilter) {
        if (selectedCategoryFilter == "ALL") journeyLogs
        else journeyLogs.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
    }

    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.US) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_journey"),
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
                    Text(
                        text = "MY JOURNEY LEDGER",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )
                    Text(
                        text = "Chronological Audit of Evolution",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Every habit completed, task conquered, knowledge logged, and XP gained is permanently recorded.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSelected = selectedCategoryFilter == cat
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) FlameGold.copy(alpha = 0.2f) else DemonSlayerDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) FlameGold else DemonSlayerBorder),
                                modifier = Modifier.clickable { selectedCategoryFilter = cat }
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

        // Timeline Items
        if (filteredLogs.isEmpty()) {
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
                        Text("No journey events recorded in this category yet.", color = TextPrimary)
                    }
                }
            }
        } else {
            items(filteredLogs) { log ->
                val timeStr = remember(log.timestampEpochMs) {
                    timeFormat.format(Date(log.timestampEpochMs))
                }

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
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (log.category) {
                                            "Evolution" -> FlameGold.copy(alpha = 0.2f)
                                            "Habits" -> CrimsonSun.copy(alpha = 0.2f)
                                            "Tasks" -> FlameAmber.copy(alpha = 0.2f)
                                            "Fitness" -> WindEmerald.copy(alpha = 0.2f)
                                            "Academics" -> MistCyan.copy(alpha = 0.2f)
                                            else -> WaterBlue.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (log.category) {
                                        "Evolution" -> "👑"
                                        "Habits" -> "⚔️"
                                        "Tasks" -> "📜"
                                        "Fitness" -> "🏋️"
                                        "Academics" -> "📚"
                                        else -> "🌸"
                                    },
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = log.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                if (log.description.isNotBlank()) {
                                    Text(
                                        text = log.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                Text(
                                    text = "${log.dateStr} at $timeStr • ${log.category}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TextTertiary
                                )
                            }
                        }

                        if (log.xpEarned > 0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FlameGold.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "+${log.xpEarned} XP",
                                    style = MaterialTheme.typography.labelSmall.copy(color = FlameGold, fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
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
}
