package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DemonSlayerDatabase
import com.example.data.model.UserProfileEntity
import com.example.ui.SamuraiScreen
import com.example.ui.theme.*

@Composable
fun SamuraiTopBar(
    userProfile: UserProfileEntity?,
    journeyWeek: Int,
    journeyDay: Int,
    rankTitle: String,
    currentScreen: SamuraiScreen,
    onNavigate: (SamuraiScreen) -> Unit,
    onQuickAdd: () -> Unit,
    onOpen3dWorld: () -> Unit
) {
    val char = DemonSlayerDatabase.getCharacter(userProfile?.characterId ?: "tanjiro")

    Surface(
        color = DemonSlayerDark.copy(alpha = 0.95f),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & App Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onNavigate(SamuraiScreen.HOME) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(CrimsonSun, DemonSlayerBlack)
                                )
                            )
                            .border(1.5.dp, FlameGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char.iconSymbol,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SAMURAI",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = CinzelFontFamily,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "侍",
                                style = MaterialTheme.typography.titleMedium,
                                color = FlameGold
                            )
                        }
                        Text(
                            text = "Week $journeyWeek • Day $journeyDay • $rankTitle",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                // Action Buttons: 3D World & Quick Add FAB
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // XP Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = DemonSlayerCardElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, FlameGold.copy(alpha = 0.4f)),
                        modifier = Modifier.clickable { onNavigate(SamuraiScreen.SAMURAI_CHAMBER) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(text = "🔥", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${userProfile?.totalXp ?: 0} XP",
                                style = MaterialTheme.typography.labelSmall.copy(color = FlameGold),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 3D World Button
                    IconButton(
                        onClick = onOpen3dWorld,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DemonSlayerCard)
                            .testTag("btn_3d_world")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "3D World",
                            tint = WaterBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Quick Add Button
                    Button(
                        onClick = onQuickAdd,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonSun),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("btn_quick_add")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Quick Add",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                    }
                }
            }

            // Quick Section Bar (Horizontal Scroll)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickNavChip("Home", Icons.Default.Home, currentScreen == SamuraiScreen.HOME) { onNavigate(SamuraiScreen.HOME) }
                QuickNavChip("Calendar", Icons.Default.CalendarMonth, currentScreen == SamuraiScreen.CALENDAR) { onNavigate(SamuraiScreen.CALENDAR) }
                QuickNavChip("Habits", Icons.Default.CheckCircle, currentScreen == SamuraiScreen.HABITS || currentScreen == SamuraiScreen.HABIT_DETAIL) { onNavigate(SamuraiScreen.HABITS) }
                QuickNavChip("Tasks", Icons.Default.Assignment, currentScreen == SamuraiScreen.TASKS) { onNavigate(SamuraiScreen.TASKS) }
                QuickNavChip("Goals", Icons.Default.Flag, currentScreen == SamuraiScreen.GOALS) { onNavigate(SamuraiScreen.GOALS) }
                QuickNavChip("Reflection", Icons.Default.AutoStories, currentScreen == SamuraiScreen.REFLECTION) { onNavigate(SamuraiScreen.REFLECTION) }
                QuickNavChip("Academics", Icons.Default.School, currentScreen == SamuraiScreen.ACADEMICS) { onNavigate(SamuraiScreen.ACADEMICS) }
                QuickNavChip("Fitness", Icons.Default.FitnessCenter, currentScreen == SamuraiScreen.FITNESS) { onNavigate(SamuraiScreen.FITNESS) }
                QuickNavChip("Health", Icons.Default.Favorite, currentScreen == SamuraiScreen.HEALTH) { onNavigate(SamuraiScreen.HEALTH) }
                QuickNavChip("Analytics", Icons.Default.BarChart, currentScreen == SamuraiScreen.ANALYTICS) { onNavigate(SamuraiScreen.ANALYTICS) }
                QuickNavChip("Growth Map", Icons.Default.Explore, currentScreen == SamuraiScreen.GROWTH_MAP) { onNavigate(SamuraiScreen.GROWTH_MAP) }
                QuickNavChip("Samurai", Icons.Default.Security, currentScreen == SamuraiScreen.SAMURAI_CHAMBER) { onNavigate(SamuraiScreen.SAMURAI_CHAMBER) }
                QuickNavChip("Journey", Icons.Default.History, currentScreen == SamuraiScreen.JOURNEY) { onNavigate(SamuraiScreen.JOURNEY) }
                QuickNavChip("Settings", Icons.Default.Settings, currentScreen == SamuraiScreen.SETTINGS) { onNavigate(SamuraiScreen.SETTINGS) }
            }
        }
    }
}

@Composable
fun QuickNavChip(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) CrimsonSun.copy(alpha = 0.25f) else DemonSlayerCard,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) CrimsonSun else DemonSlayerBorder
        ),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (selected) CrimsonSunGlow else TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (selected) TextPrimary else TextSecondary
            )
        }
    }
}

@Composable
fun SamuraiBottomBar(
    currentScreen: SamuraiScreen,
    onNavigate: (SamuraiScreen) -> Unit
) {
    Surface(
        color = DemonSlayerDark,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        NavigationBar(
            containerColor = DemonSlayerDark,
            contentColor = TextPrimary,
            modifier = Modifier.height(64.dp)
        ) {
            val items = listOf(
                Triple(SamuraiScreen.HOME, "Home", Icons.Default.Home),
                Triple(SamuraiScreen.CALENDAR, "Calendar", Icons.Default.CalendarMonth),
                Triple(SamuraiScreen.HABITS, "Habits", Icons.Default.CheckCircle),
                Triple(SamuraiScreen.TASKS, "Tasks", Icons.Default.Assignment),
                Triple(SamuraiScreen.REFLECTION, "Garden", Icons.Default.AutoStories),
                Triple(SamuraiScreen.WORLD_3D, "3D World", Icons.Default.Public)
            )

            items.forEach { (screen, label, icon) ->
                val isSelected = currentScreen == screen ||
                        (screen == SamuraiScreen.HABITS && currentScreen == SamuraiScreen.HABIT_DETAIL)
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onNavigate(screen) },
                    icon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CrimsonSun,
                        selectedTextColor = CrimsonSunGlow,
                        unselectedIconColor = TextTertiary,
                        unselectedTextColor = TextTertiary,
                        indicatorColor = CrimsonSun.copy(alpha = 0.15f)
                    )
                )
            }
        }
    }
}
