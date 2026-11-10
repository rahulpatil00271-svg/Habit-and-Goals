package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.SamuraiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class SamuraiScreen {
    HOME,
    CALENDAR,
    HABITS,
    HABIT_DETAIL,
    TASKS,
    GOALS,
    REFLECTION,
    ACADEMICS,
    FITNESS,
    HEALTH,
    JOURNEY,
    GROWTH_MAP,
    ANALYTICS,
    SAMURAI_CHAMBER,
    SETTINGS,
    WORLD_3D
}

data class UiState(
    val currentScreen: SamuraiScreen = SamuraiScreen.HOME,
    val screenBackStack: List<SamuraiScreen> = listOf(SamuraiScreen.HOME),
    val selectedHabitId: Long? = null,
    val selectedCalendarDate: String = "",
    val quickAddOpen: Boolean = false,
    val userProfile: UserProfileEntity? = null,
    val journeyWeek: Int = 1,
    val journeyDay: Int = 1,
    val rankTitle: String = "Mizunoto",
    val todayDateStr: String = ""
)

class SamuraiViewModel(application: Application) : AndroidViewModel(application) {
    val repository = SamuraiRepository(AppDatabase.getInstance(application))

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Database reactive streams
    val userProfileFlow = repository.userDao.getUser()
    val habitsFlow = repository.habitDao.getAllActiveHabits()
    val allHabitsFlow = repository.habitDao.getAllHabits()
    val habitLogsFlow = repository.habitDao.getAllLogs()
    val tasksFlow = repository.taskDao.getAllTasks()
    val goalsFlow = repository.goalDao.getAllGoals()
    val journalsFlow = repository.reflectionDao.getAllJournals()
    val reflectionAnswersFlow = repository.reflectionDao.getAllReflectionAnswers()
    val selfReflectionsFlow = repository.reflectionDao.getAllSelfReflections()
    val subjectsFlow = repository.academicDao.getAllSubjects()
    val studySessionsFlow = repository.academicDao.getAllStudySessions()
    val examsFlow = repository.academicDao.getAllExams()
    val workoutsFlow = repository.fitnessDao.getAllWorkouts()
    val skillsFlow = repository.fitnessDao.getAllSkills()
    val healthRecordsFlow = repository.healthDao.getAllHealthRecords()
    val journeyLogsFlow = repository.journeyDao.getAllLogs()
    val missionsFlow = repository.missionDao.getAllMissions()

    init {
        val today = repository.getTodayDateStr()
        _uiState.update { it.copy(todayDateStr = today, selectedCalendarDate = today) }

        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }

        viewModelScope.launch {
            userProfileFlow.collect { profile ->
                if (profile != null) {
                    val (week, day) = repository.calculateJourneyWeekAndDay(profile.startDateEpochMs)
                    val rank = repository.calculateRankTitle(week)
                    _uiState.update {
                        it.copy(
                            userProfile = profile,
                            journeyWeek = week,
                            journeyDay = day,
                            rankTitle = rank
                        )
                    }
                }
            }
        }
    }

    fun navigateTo(screen: SamuraiScreen, habitId: Long? = null) {
        _uiState.update { state ->
            val newStack = if (state.currentScreen != screen) {
                state.screenBackStack + screen
            } else state.screenBackStack
            state.copy(
                currentScreen = screen,
                screenBackStack = newStack,
                selectedHabitId = habitId ?: state.selectedHabitId
            )
        }
    }

    fun navigateBack(): Boolean {
        val currentStack = _uiState.value.screenBackStack
        if (currentStack.size > 1) {
            val updatedStack = currentStack.dropLast(1)
            val previousScreen = updatedStack.last()
            _uiState.update {
                it.copy(
                    currentScreen = previousScreen,
                    screenBackStack = updatedStack
                )
            }
            return true
        }
        return false
    }

    fun setSelectedCalendarDate(dateStr: String) {
        _uiState.update { it.copy(selectedCalendarDate = dateStr) }
    }

    fun setQuickAddOpen(isOpen: Boolean) {
        _uiState.update { it.copy(quickAddOpen = isOpen) }
    }

    // --- Habit Actions ---
    fun toggleHabitCompletion(habitId: Long, dateStr: String, currentCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val newStatus = !currentCompleted
            repository.habitDao.insertOrUpdateLog(
                HabitLogEntity(
                    habitId = habitId,
                    dateStr = dateStr,
                    completed = newStatus,
                    progressValue = if (newStatus) 1.0f else 0.0f,
                    timestampEpochMs = System.currentTimeMillis()
                )
            )
            if (newStatus) {
                repository.awardXp(
                    amount = 25,
                    category = "Habits",
                    actionType = "HABIT_COMPLETED",
                    title = "Habit Completed",
                    description = "Mastered daily discipline rep for date $dateStr"
                )
            }
        }
    }

    fun saveHabit(habit: HabitEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (habit.id == 0L) {
                repository.habitDao.insert(habit)
                repository.awardXp(30, "Habits", "HABIT_CREATED", "New Kata Begun", habit.name)
            } else {
                repository.habitDao.update(habit)
            }
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.habitDao.delete(habit)
        }
    }

    // --- Task Actions ---
    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedAtEpochMs = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.taskDao.update(updated)
            if (updated.isCompleted) {
                repository.awardXp(30, "Tasks", "TASK_COMPLETED", "Mission Accomplished", task.title)
            }
        }
    }

    fun saveTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (task.id == 0L) {
                repository.taskDao.insert(task)
                repository.awardXp(20, "Tasks", "TASK_CREATED", "Mission Scheduled", task.title)
            } else {
                repository.taskDao.update(task)
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.taskDao.delete(task)
        }
    }

    // --- Goal Actions ---
    fun saveGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (goal.id == 0L) {
                repository.goalDao.insert(goal)
                repository.awardXp(50, "Goals", "GOAL_CREATED", "Grand Objective Set", goal.title)
            } else {
                repository.goalDao.update(goal)
            }
        }
    }

    fun updateGoalProgress(goal: GoalEntity, delta: Float, notes: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val newCurrent = (goal.currentValue + delta).coerceAtLeast(0f)
            val isNowCompleted = newCurrent >= goal.targetValue
            val updatedGoal = goal.copy(
                currentValue = newCurrent,
                status = if (isNowCompleted) "COMPLETED" else goal.status
            )
            repository.goalDao.update(updatedGoal)
            repository.goalDao.insertProgressLog(
                GoalProgressEntity(
                    goalId = goal.id,
                    dateStr = repository.getTodayDateStr(),
                    deltaValue = delta,
                    currentValue = newCurrent,
                    notes = notes
                )
            )
            val xpGain = if (isNowCompleted) 150 else 30
            repository.awardXp(
                xpGain,
                "Goals",
                if (isNowCompleted) "GOAL_MASTERED" else "GOAL_PROGRESS",
                if (isNowCompleted) "Goal Achieved: ${goal.title}" else "Progress Made: ${goal.title}",
                "+$delta ${goal.unit}"
            )
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.goalDao.delete(goal)
        }
    }

    // --- Reflection & Journal Actions ---
    fun saveJournal(journal: JournalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (journal.id == 0L) {
                repository.reflectionDao.insertJournal(journal)
                repository.awardXp(40, "Mind", "JOURNAL_SAVED", "Scroll Inscribed", journal.title)
            } else {
                repository.reflectionDao.updateJournal(journal.copy(lastEditedEpochMs = System.currentTimeMillis()))
            }
        }
    }

    fun deleteJournal(journal: JournalEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.reflectionDao.deleteJournal(journal)
        }
    }

    fun saveReflectionAnswer(dateStr: String, index: Int, questionText: String, answer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.reflectionDao.insertOrUpdateAnswer(
                ReflectionAnswerEntity(
                    dateStr = dateStr,
                    questionIndex = index,
                    questionText = questionText,
                    answerText = answer,
                    updatedAtEpochMs = System.currentTimeMillis()
                )
            )
            repository.awardXp(15, "Mind", "REFLECTION_ANSWER", "Inner Sight Refined", "Answered Question #$index")
        }
    }

    fun saveSelfReflection(sectionKey: String, title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.reflectionDao.insertOrUpdateSelfReflection(
                SelfReflectionEntity(
                    sectionKey = sectionKey,
                    title = title,
                    content = content,
                    updatedAtEpochMs = System.currentTimeMillis()
                )
            )
            repository.awardXp(30, "Mind", "SELF_REFLECTION", "Pillar of Self Refined", title)
        }
    }

    // --- Academic Actions ---
    fun logStudySession(subjectName: String, topic: String, durationMinutes: Int, notes: String, sessionType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.academicDao.insertStudySession(
                StudySessionEntity(
                    subjectName = subjectName,
                    topic = topic,
                    dateStr = repository.getTodayDateStr(),
                    durationMinutes = durationMinutes,
                    notes = notes,
                    sessionType = sessionType
                )
            )
            val xp = (durationMinutes / 15 * 10).coerceAtLeast(20)
            repository.awardXp(xp, "Academics", "STUDY_LOGGED", "Wisdom Cultivated", "$durationMinutes mins on $subjectName")
        }
    }

    fun saveSubject(subject: AcademicSubjectEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (subject.id == 0L) {
                repository.academicDao.insertSubject(subject)
            } else {
                repository.academicDao.updateSubject(subject)
            }
        }
    }

    // --- Fitness Actions ---
    fun logWorkout(name: String, category: String, durationMinutes: Int, exercisesSummary: String, notes: String, isPr: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fitnessDao.insertWorkout(
                WorkoutEntity(
                    name = name,
                    category = category,
                    dateStr = repository.getTodayDateStr(),
                    durationMinutes = durationMinutes,
                    exercisesSummary = exercisesSummary,
                    notes = notes,
                    isPr = isPr
                )
            )
            val xp = if (isPr) 80 else 50
            repository.awardXp(xp, "Fitness", if (isPr) "FITNESS_PR" else "WORKOUT_LOGGED", if (isPr) "New Personal Record!" else "Dojo Training Complete", name)
        }
    }

    fun updateSkillStage(skill: SkillProgressEntity, newStage: Int, stageTitle: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = skill.copy(
                currentStage = newStage,
                stageTitle = stageTitle,
                notes = notes,
                lastUpdatedStr = repository.getTodayDateStr()
            )
            repository.fitnessDao.insertOrUpdateSkill(updated)
            repository.awardXp(75, "Fitness", "SKILL_PROGRESSED", "Calisthenic Form Mastery", "${skill.skillName} upgraded to Stage $newStage")
        }
    }

    // --- Health Actions ---
    fun logHealth(sleepHours: Float, sleepQuality: Int, hydrationMl: Int, recoveryScore: Int, energyLevel: Int, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.healthDao.insertOrUpdateHealth(
                HealthEntity(
                    dateStr = repository.getTodayDateStr(),
                    sleepHours = sleepHours,
                    sleepQuality = sleepQuality,
                    hydrationMl = hydrationMl,
                    recoveryScore = recoveryScore,
                    energyLevel = energyLevel,
                    notes = notes
                )
            )
            repository.awardXp(30, "Health", "HEALTH_LOGGED", "Body & Spirit Restored", "Recovery Score: $recoveryScore%")
        }
    }

    // --- Character & Settings Actions ---
    fun selectCharacter(characterId: String, breathingStyle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.userDao.getUserSync()
            if (current != null) {
                repository.userDao.insertOrUpdate(
                    current.copy(
                        characterId = characterId,
                        breathingStyle = breathingStyle
                    )
                )
                repository.awardXp(50, "Evolution", "CHARACTER_SWITCHED", "Breathing Technique Awakened", breathingStyle)
            }
        }
    }

    fun selectBreathingPath(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.userDao.getUserSync()
            if (current != null) {
                repository.userDao.insertOrUpdate(current.copy(activeBreathingPath = path))
            }
        }
    }

    fun updateSettings(sound: Boolean, reducedMotion: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val current = repository.userDao.getUserSync()
            if (current != null) {
                repository.userDao.insertOrUpdate(
                    current.copy(
                        soundEnabled = sound,
                        reducedMotion = reducedMotion
                    )
                )
            }
        }
    }
}
