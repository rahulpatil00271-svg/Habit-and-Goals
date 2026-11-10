package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.SamuraiViewModel
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SettingsScreen(
    viewModel: SamuraiViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val userProfile = uiState.userProfile

    var soundEnabled by remember(userProfile) { mutableStateOf(userProfile?.soundEnabled ?: true) }
    var reducedMotion by remember(userProfile) { mutableStateOf(userProfile?.reducedMotion ?: false) }

    val startDateFormat = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.US) }
    val startDateStr = remember(userProfile?.startDateEpochMs) {
        if (userProfile != null) startDateFormat.format(Date(userProfile.startDateEpochMs)) else "Today"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DemonSlayerBlack)
            .testTag("screen_settings"),
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
                    Text(
                        text = "SETTINGS SANCTUARY",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, letterSpacing = 1.sp),
                        color = FlameGold
                    )
                    Text(
                        text = "Preferences & System Configuration",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontFamily = CinzelFontFamily),
                        color = TextPrimary
                    )
                }
            }
        }

        // Journey Account Details
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ACCOUNT & JOURNEY TIMELINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily),
                        color = FlameGold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Journey Commenced: $startDateStr",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Current State: Week ${uiState.journeyWeek}, Day ${uiState.journeyDay} (Level ${uiState.journeyWeek})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "Slayer Rank: ${uiState.rankTitle}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CrimsonSunGlow
                    )
                    Text(
                        text = "Total XP Gathered: ${userProfile?.totalXp ?: 0} XP",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlameGold
                    )
                }
            }
        }

        // Preferences Toggles
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DemonSlayerBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "APPLICATION PREFERENCES",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily),
                        color = FlameGold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Sword & Combat Sound Effects", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            Text("Play Katana audio feedback on task & habit completion", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                viewModel.updateSettings(soundEnabled, reducedMotion)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = CrimsonSun, checkedTrackColor = CrimsonSunDark)
                        )
                    }

                    Divider(color = DemonSlayerBorder, modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Reduced Motion Mode", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = TextPrimary)
                            Text("Simplify particle transitions and 3D camera sweeps", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Switch(
                            checked = reducedMotion,
                            onCheckedChange = {
                                reducedMotion = it
                                viewModel.updateSettings(soundEnabled, reducedMotion)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = FlameGold, checkedTrackColor = FlameAmber)
                        )
                    }
                }
            }
        }

        // Database & Cloud Persistence Status
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DemonSlayerCardElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, WindEmerald.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(10.dp).clip(androidx.compose.foundation.shape.CircleShape).background(WindEmerald))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DATABASE STATUS: ACTIVE & SECURED",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, fontWeight = FontWeight.Bold),
                            color = WindEmerald
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SAMURAI SQLite Room Database is encrypted and running locally on device. All habits, tasks, goals, academic sessions, workouts, and journals persist automatically across app launches.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Version & Credits
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SAMURAI • THE 3D RPG PERSONAL GROWTH OPERATING SYSTEM",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = CinzelFontFamily, fontSize = 9.sp),
                    color = TextTertiary
                )
                Text(
                    text = "Version 1.0 • Demon Slayer Universe Edition",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = TextTertiary
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
