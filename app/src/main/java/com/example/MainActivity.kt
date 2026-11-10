package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ui.SamuraiScreen
import com.example.ui.SamuraiViewModel
import com.example.ui.components.Interactive3DWorldScreen
import com.example.ui.components.QuickAddDialog
import com.example.ui.components.SamuraiBottomBar
import com.example.ui.components.SamuraiTopBar
import com.example.ui.screens.*
import com.example.ui.theme.DemonSlayerBlack
import com.example.ui.theme.SamuraiTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SamuraiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SamuraiTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Handle system back navigation
                BackHandler(enabled = uiState.currentScreen != SamuraiScreen.HOME) {
                    if (!viewModel.navigateBack()) {
                        viewModel.navigateTo(SamuraiScreen.HOME)
                    }
                }

                if (uiState.currentScreen == SamuraiScreen.WORLD_3D) {
                    // Full screen 3D world with camera manipulation
                    Interactive3DWorldScreen(
                        onNavigate = { screen ->
                            viewModel.navigateTo(screen)
                        },
                        onClose = {
                            viewModel.navigateBack()
                        }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DemonSlayerBlack),
                        topBar = {
                            SamuraiTopBar(
                                userProfile = uiState.userProfile,
                                journeyWeek = uiState.journeyWeek,
                                journeyDay = uiState.journeyDay,
                                rankTitle = uiState.rankTitle,
                                currentScreen = uiState.currentScreen,
                                onNavigate = { screen -> viewModel.navigateTo(screen) },
                                onQuickAdd = { viewModel.setQuickAddOpen(true) },
                                onOpen3dWorld = { viewModel.navigateTo(SamuraiScreen.WORLD_3D) }
                            )
                        },
                        bottomBar = {
                            SamuraiBottomBar(
                                currentScreen = uiState.currentScreen,
                                onNavigate = { screen -> viewModel.navigateTo(screen) }
                            )
                        },
                        containerColor = DemonSlayerBlack
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Crossfade(
                                targetState = uiState.currentScreen,
                                label = "ScreenTransition"
                            ) { screen ->
                                when (screen) {
                                    SamuraiScreen.HOME -> HomeScreen(
                                        viewModel = viewModel,
                                        onNavigate = { target, id -> viewModel.navigateTo(target, id) }
                                    )
                                    SamuraiScreen.CALENDAR -> UniversalCalendarScreen(
                                        viewModel = viewModel,
                                        onNavigate = { target, id -> viewModel.navigateTo(target, id) },
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.HABITS -> HabitsScreen(
                                        viewModel = viewModel,
                                        onOpenHabitDetail = { id -> viewModel.navigateTo(SamuraiScreen.HABIT_DETAIL, id) },
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.HABIT_DETAIL -> HabitDetailScreen(
                                        habitId = uiState.selectedHabitId ?: 1L,
                                        viewModel = viewModel,
                                        onBack = { viewModel.navigateBack() }
                                    )
                                    SamuraiScreen.TASKS -> TasksScreen(
                                        viewModel = viewModel,
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.GOALS -> GoalsScreen(
                                        viewModel = viewModel,
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.REFLECTION -> ReflectionScreen(
                                        viewModel = viewModel,
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.ACADEMICS -> AcademicsScreen(
                                        viewModel = viewModel,
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.FITNESS -> FitnessScreen(
                                        viewModel = viewModel,
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.HEALTH -> HealthScreen(
                                        viewModel = viewModel,
                                        onOpenQuickAdd = { viewModel.setQuickAddOpen(true) }
                                    )
                                    SamuraiScreen.JOURNEY -> MyJourneyScreen(
                                        viewModel = viewModel
                                    )
                                    SamuraiScreen.GROWTH_MAP -> GrowthMapScreen(
                                        viewModel = viewModel,
                                        onNavigate = { target, id -> viewModel.navigateTo(target, id) }
                                    )
                                    SamuraiScreen.ANALYTICS -> AnalyticsScreen(
                                        viewModel = viewModel
                                    )
                                    SamuraiScreen.SAMURAI_CHAMBER -> SamuraiChamberScreen(
                                        viewModel = viewModel
                                    )
                                    SamuraiScreen.SETTINGS -> SettingsScreen(
                                        viewModel = viewModel
                                    )
                                    SamuraiScreen.WORLD_3D -> {
                                        // Handled above
                                    }
                                }
                            }
                        }
                    }
                }

                // Global Quick Add Modal
                if (uiState.quickAddOpen) {
                    QuickAddDialog(
                        viewModel = viewModel,
                        onDismiss = { viewModel.setQuickAddOpen(false) }
                    )
                }
            }
        }
    }
}
