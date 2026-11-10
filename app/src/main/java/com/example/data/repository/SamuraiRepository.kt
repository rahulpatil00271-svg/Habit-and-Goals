package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class CalendarDaySummary(
    val dateStr: String,
    val habitsCount: Int = 0,
    val habitsCompleted: Int = 0,
    val tasksCount: Int = 0,
    val tasksCompleted: Int = 0,
    val goalsCount: Int = 0,
    val studyMinutes: Int = 0,
    val workoutCount: Int = 0,
    val hasJournal: Boolean = false,
    val hasReflection: Boolean = false,
    val hasHealth: Boolean = false
)

data class HabitStatistics(
    val habit: HabitEntity,
    val totalScheduled: Int,
    val totalCompleted: Int,
    val completionPercentage: Float,
    val currentStreak: Int,
    val longestStreak: Int,
    val heatmapData: Map<String, Int> // dateStr -> 0: none, 1: completed, 2: partial, -1: missed
)

data class GlobalAnalyticsData(
    val overallScore: Float,
    val habitCompletionRate: Float,
    val taskCompletionRate: Float,
    val goalProgressAverage: Float,
    val studyHoursActual: Float,
    val studyHoursTarget: Float,
    val workoutConsistencyRate: Float,
    val totalActivitiesCompleted: Int,
    val totalXpEarned: Long,
    val periodLabel: String
)

class SamuraiRepository(private val db: AppDatabase) {
    val userDao = db.userDao()
    val habitDao = db.habitDao()
    val taskDao = db.taskDao()
    val goalDao = db.goalDao()
    val reflectionDao = db.reflectionDao()
    val academicDao = db.academicDao()
    val fitnessDao = db.fitnessDao()
    val healthDao = db.healthDao()
    val journeyDao = db.journeyDao()
    val missionDao = db.missionDao()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayDateStr(): String = dateFormat.format(Date())

    suspend fun initializeDefaultDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingUser = userDao.getUserSync()
        if (existingUser == null) {
            val now = System.currentTimeMillis()
            // Week 1, Day 1 Start
            userDao.insertOrUpdate(
                UserProfileEntity(
                    id = 1,
                    username = "Demon Slayer",
                    startDateEpochMs = now,
                    characterId = "tanjiro",
                    breathingStyle = "Sun & Water Breathing",
                    activeBreathingPath = "Discipline",
                    totalXp = 0L,
                    rankTitle = "Mizunoto"
                )
            )

            // Initial Core Habits
            habitDao.insert(
                HabitEntity(
                    name = "Morning Kata & Movement",
                    description = "Demon Slayer body awakening: stretching, breathwork and core",
                    category = "Fitness",
                    icon = "sword",
                    colorHex = "#E53935",
                    frequency = "DAILY",
                    dailyTarget = 1f,
                    unit = "sessions",
                    priority = "HIGH"
                )
            )
            habitDao.insert(
                HabitEntity(
                    name = "Deep Focus Study Session",
                    description = "Immersive academic or technical training block",
                    category = "Academics",
                    icon = "book",
                    colorHex = "#00B0FF",
                    frequency = "DAILY",
                    dailyTarget = 2f,
                    unit = "hours",
                    priority = "HIGH"
                )
            )
            habitDao.insert(
                HabitEntity(
                    name = "Physical Strength & Conditioning",
                    description = "Calisthenics, gym or stamina training",
                    category = "Fitness",
                    icon = "dumbbell",
                    colorHex = "#FFB300",
                    frequency = "DAILY",
                    dailyTarget = 1f,
                    unit = "workouts",
                    priority = "HIGH"
                )
            )
            habitDao.insert(
                HabitEntity(
                    name = "Evening Reflection & Journal",
                    description = "Record lessons learned, gratitude and tomorrow's mission",
                    category = "Mind",
                    icon = "feather",
                    colorHex = "#BA68C8",
                    frequency = "DAILY",
                    dailyTarget = 1f,
                    unit = "entries",
                    priority = "MEDIUM"
                )
            )

            // Initial Sample Goals
            goalDao.insert(
                GoalEntity(
                    title = "Master Total Concentration Breathing",
                    description = "Maintain constant deep abdominal breathing throughout all daily tasks",
                    category = "Discipline",
                    type = "MONTHLY",
                    startDateStr = getTodayDateStr(),
                    deadlineStr = getDateOffsetStr(30),
                    targetValue = 100f,
                    currentValue = 15f,
                    unit = "%",
                    priority = "HIGH",
                    milestonesJson = """[{"id":1,"name":"Morning breathwork routine","isDone":true},{"id":2,"name":"Maintain focus during study","isDone":false},{"id":3,"name":"Full day total concentration","isDone":false}]"""
                )
            )
            goalDao.insert(
                GoalEntity(
                    title = "Unlock 10 Clean Handstand Push-ups",
                    description = "Achieve full body control and overhead pressing power",
                    category = "Fitness",
                    type = "QUARTERLY",
                    startDateStr = getTodayDateStr(),
                    deadlineStr = getDateOffsetStr(90),
                    targetValue = 10f,
                    currentValue = 2f,
                    unit = "reps",
                    priority = "HIGH",
                    milestonesJson = """[{"id":1,"name":"60s Wall handstand hold","isDone":true},{"id":2,"name":"10 Pike push-ups with elevated feet","isDone":true},{"id":3,"name":"Wall assisted handstand push-up","isDone":false}]"""
                )
            )

            // Initial Academic Subjects
            academicDao.insertSubject(
                AcademicSubjectEntity(
                    name = "Computer Science & Architecture",
                    code = "CS-401",
                    colorHex = "#00B0FF",
                    targetWeeklyHours = 12f,
                    completedTopics = 4,
                    totalTopics = 12
                )
            )
            academicDao.insertSubject(
                AcademicSubjectEntity(
                    name = "Mathematics & Algorithm Design",
                    code = "MATH-302",
                    colorHex = "#7E57C2",
                    targetWeeklyHours = 8f,
                    completedTopics = 3,
                    totalTopics = 10
                )
            )

            // Initial Calisthenics Progression Skills
            val skills = listOf(
                SkillProgressEntity(skillName = "Handstand", currentStage = 1, maxStage = 5, stageTitle = "Stage 1: Wall Plank & Balance", notes = "Mastering hollow body position and wrist prep"),
                SkillProgressEntity(skillName = "Muscle-up", currentStage = 1, maxStage = 5, stageTitle = "Stage 1: Chest-to-Bar Explosive Pull-up", notes = "Building pulling velocity and straight-bar dips"),
                SkillProgressEntity(skillName = "Pull-ups", currentStage = 2, maxStage = 5, stageTitle = "Stage 2: Strict Clean 10 Reps", notes = "Full lock-out and hollow body form"),
                SkillProgressEntity(skillName = "Push-ups", currentStage = 3, maxStage = 5, stageTitle = "Stage 3: Diamond & Archer Push-ups", notes = "Unbroken sets with full range of motion"),
                SkillProgressEntity(skillName = "L-Sit", currentStage = 1, maxStage = 5, stageTitle = "Stage 1: Tuck L-Sit Hold (20s)", notes = "Depressing shoulders and engaging hip flexors"),
                SkillProgressEntity(skillName = "Pistol Squat", currentStage = 1, maxStage = 5, stageTitle = "Stage 1: Assisted Box Step-downs", notes = "Ankle dorsiflexion and unilateral control")
            )
            skills.forEach { fitnessDao.insertOrUpdateSkill(it) }

            // Initial Default Self-Reflections
            val selfReflections = listOf(
                SelfReflectionEntity(sectionKey = "STRENGTHS", title = "My Core Strengths", content = "Relentless discipline, ability to lock into deep focus, unwavering pursuit of excellence."),
                SelfReflectionEntity(sectionKey = "WEAKNESSES", title = "My Current Weaknesses", content = "Occasional perfectionism causing hesitation; need to maintain consistent recovery and sleep."),
                SelfReflectionEntity(sectionKey = "MISTAKES", title = "Mistakes & Lessons", content = "Pushing through fatigue without proper warm-up; prioritizing ego over sustainable mastery."),
                SelfReflectionEntity(sectionKey = "FEARS", title = "Fears to Conquer", content = "Fear of stagnation; fear of letting down my potential. I conquer this through daily decisive action."),
                SelfReflectionEntity(sectionKey = "VALUES", title = "My Unshakable Values", content = "Honor, relentless work ethic, humility, lifelong curiosity, inner calm in turmoil.")
            )
            selfReflections.forEach { reflectionDao.insertOrUpdateSelfReflection(it) }

            // Initial RPG Missions
            missionDao.insertMission(
                MissionEntity(title = "Daily Training Kata", description = "Complete 3 daily habits today", category = "Discipline", type = "DAILY", xpReward = 50, maxProgress = 3)
            )
            missionDao.insertMission(
                MissionEntity(title = "Scholarly Endeavor", description = "Record at least 1 hour of deep study", category = "Academics", type = "DAILY", xpReward = 40, maxProgress = 1)
            )
            missionDao.insertMission(
                MissionEntity(title = "Evening Sword Cleaning", description = "Write a daily journal or reflection entry", category = "Mind", type = "DAILY", xpReward = 35, maxProgress = 1)
            )
            missionDao.insertMission(
                MissionEntity(title = "Iron Discipline", description = "Maintain a 5-day streak across all habits", category = "Discipline", type = "WEEKLY", xpReward = 150, maxProgress = 5)
            )
            missionDao.insertMission(
                MissionEntity(title = "Hashira Awakening", description = "Reach Week 2 and earn 500 Total XP", category = "Special", type = "SPECIAL", xpReward = 300, maxProgress = 500)
            )

            // Log Journey Beginning
            journeyDao.insertLog(
                JourneyLogEntity(
                    dateStr = getTodayDateStr(),
                    category = "Evolution",
                    actionType = "JOURNEY_BEGUN",
                    title = "The Samurai Journey Begun",
                    description = "Initialized Demon Slayer Path at Week 1, Day 1. Welcome to SAMURAI.",
                    xpEarned = 100
                )
            )
            userDao.addXp(100)
        }
    }

    fun getDateOffsetStr(daysOffset: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysOffset)
        return dateFormat.format(cal.time)
    }

    suspend fun awardXp(amount: Int, category: String, actionType: String, title: String, description: String = "") {
        withContext(Dispatchers.IO) {
            userDao.addXp(amount)
            journeyDao.insertLog(
                JourneyLogEntity(
                    dateStr = getTodayDateStr(),
                    category = category,
                    actionType = actionType,
                    title = title,
                    description = description,
                    xpEarned = amount
                )
            )
        }
    }

    fun calculateJourneyWeekAndDay(startDateEpochMs: Long): Pair<Int, Int> {
        val now = System.currentTimeMillis()
        val diffMs = (now - startDateEpochMs).coerceAtLeast(0L)
        val days = (diffMs / TimeUnit.DAYS.toMillis(1)).toInt() + 1
        val week = ((days - 1) / 7) + 1
        val dayInWeek = ((days - 1) % 7) + 1
        return Pair(week, dayInWeek)
    }

    fun calculateRankTitle(week: Int): String {
        return when {
            week <= 5 -> "Mizunoto (癸) - Rank 1"
            week <= 10 -> "Mizunoe (壬) - Rank 2"
            week <= 15 -> "Kanoto (辛) - Rank 3"
            week <= 20 -> "Kanoe (庚) - Rank 4"
            week <= 25 -> "Tsuchinoto (己) - Rank 5"
            week <= 30 -> "Tsuchinoe (戊) - Rank 6"
            week <= 35 -> "Hinoto (丁) - Rank 7"
            week <= 40 -> "Hinoe (丙) - Rank 8"
            week <= 45 -> "Kinoto (乙) - Rank 9"
            week <= 51 -> "Kinoe (甲) - Rank 10"
            else -> "Hashira (柱) - Supreme Rank"
        }
    }
}
