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
import com.example.data.model.HealthEntity
import com.example.ui.SamuraiViewModel
import com.example.ui.components.StatBox
import com.example.ui.components.customTextFieldColors
import com.example.ui.theme.*

@Composable
fun HealthScreen(
    viewModel: SamuraiViewModel,
    onOpenQuickAdd: () -> Unit
) {
    val healthRecords by viewModel.healthRecordsFlow.collectAsState(initial = emptyList())
    val uiState by viewModel.uiState.collectAsState()
    val todayStr = uiState.todayDateStr

    val todayHealth = remember(healthRecords, todayStr) { healthRecords.find { it.dateStr == todayStr } }
    val avgSleep = remember(healthRecords) {
        if (healthRecords.isNotEmpty()) healthRecords.map { it.sleepHours }.average().toFloat() else 7.5f
    }
    val avgRecovery = remember(healthRecords) {
        if (healthRecords.isNotEmpty()) healthRecords.map { it.recoveryScore }.average().toInt() else 85
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_health"),
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
                                text = "HEALTH SANCTUARY",
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                                color = WindEmerald
                            )
                            Text(
                                text = "Vitality, Sleep & Recovery",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                                color = TextPrimary
                            )
                        }

                        Button(
                            onClick = onOpenQuickAdd,
                            colors = ButtonDefaults.buttonColors(containerColor = WindEmerald),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Log Vitality", tint = DemonSlayerBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Vitality", color = DemonSlayerBlack, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatBox("Avg Sleep", String.format("%.1fh", avgSleep), WaterBlue, Modifier.weight(1f))
                        StatBox("Avg Recovery", "$avgRecovery%", WindEmerald, Modifier.weight(1f))
                        StatBox("Days Tracked", "${healthRecords.size} Days", FlameGold, Modifier.weight(1f))
                    }
                }
            }
        }

        // Today's Vitality Status
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, WindEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "🌿 TODAY'S VITALITY MATRIX ($todayStr)",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = WindEmerald
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (todayHealth != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Sleep Duration", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("${todayHealth.sleepHours} Hours", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            }
                            Column {
                                Text("Hydration", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("${todayHealth.hydrationMl} ml", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = WaterBlue)
                            }
                            Column {
                                Text("Recovery Score", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                                Text("${todayHealth.recoveryScore}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = WindEmerald)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Sleep Quality: ${todayHealth.sleepQuality} ★ • Energy Level: ${todayHealth.energyLevel}/10",
                            style = MaterialTheme.typography.bodySmall,
                            color = FlameGold
                        )
                    } else {
                        Text(
                            text = "No vitality log recorded for today yet. Use 'Log Vitality' above to register your sleep, water, and recovery score.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // Past Health Logs
        item {
            Text(
                text = "📜 HISTORICAL HEALTH RECORDS",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = CinzelFontFamily,
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = FlameGold
            )
        }

        if (healthRecords.isEmpty()) {
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
                        Text("No historical health records yet.", color = TextSecondary)
                    }
                }
            }
        } else {
            items(healthRecords) { h ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Date: ${h.dateStr}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = TextPrimary
                            )
                            Text(
                                text = "Sleep: ${h.sleepHours}h (${h.sleepQuality}★) • Hydration: ${h.hydrationMl}ml • Recovery: ${h.recoveryScore}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
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
