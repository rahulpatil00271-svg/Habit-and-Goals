package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val username: String = "Demon Slayer",
    val startDateEpochMs: Long = System.currentTimeMillis(),
    val characterId: String = "tanjiro",
    val breathingStyle: String = "Water & Sun Breathing",
    val activeBreathingPath: String = "Discipline",
    val totalXp: Long = 0L,
    val rankTitle: String = "Mizunoto", // Mizunoto (Rank 1) -> Hashira (Rank 10)
    val soundEnabled: Boolean = true,
    val reducedMotion: Boolean = false,
    val cloudSyncEnabled: Boolean = false
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: String = "Discipline", // Discipline, Fitness, Mind, Academics, Health, Creator
    val icon: String = "sword",
    val colorHex: String = "#E53935",
    val frequency: String = "DAILY", // DAILY, WEEKDAYS, CUSTOM
    val dailyTarget: Float = 1.0f,
    val unit: String = "times",
    val priority: String = "HIGH", // HIGH, MEDIUM, LOW
    val isPaused: Boolean = false,
    val isArchived: Boolean = false,
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_logs",
    indices = [Index(value = ["habitId", "dateStr"], unique = true)]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val dateStr: String, // "YYYY-MM-DD"
    val completed: Boolean,
    val progressValue: Float = 1.0f,
    val notes: String = "",
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Discipline",
    val priority: String = "HIGH", // HIGH, MEDIUM, LOW
    val dueDateStr: String = "", // "YYYY-MM-DD"
    val dueTimeStr: String = "", // "HH:mm"
    val isCompleted: Boolean = false,
    val completedAtEpochMs: Long? = null,
    val subtasksJson: String = "[]", // [{"title":"step1","isDone":true}]
    val tags: String = "",
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Discipline", // Academics, Fitness, Health, Discipline, Knowledge, Creativity, Content Creation, Personal Development, Spirituality, Custom
    val type: String = "MONTHLY", // DAILY, WEEKLY, MONTHLY, QUARTERLY, ANNUAL, LONG_TERM
    val startDateStr: String = "",
    val deadlineStr: String = "", // "YYYY-MM-DD"
    val targetValue: Float = 100f,
    val currentValue: Float = 0f,
    val unit: String = "%",
    val priority: String = "HIGH",
    val status: String = "ACTIVE", // ACTIVE, COMPLETED, PAUSED
    val milestonesJson: String = "[]", // [{"id":1,"name":"milestone","targetDate":"","isDone":false}]
    val createdAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "goal_progress_logs")
data class GoalProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val goalId: Long,
    val dateStr: String,
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val deltaValue: Float,
    val currentValue: Float,
    val notes: String = ""
)

@Entity(tableName = "journals")
data class JournalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val dateStr: String, // "YYYY-MM-DD"
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val lastEditedEpochMs: Long = System.currentTimeMillis(),
    val tags: String = "",
    val mood: String = "Focused", // Peaceful, Motivated, Focused, Challenged, Victorious, Contemplative
    val isPinned: Boolean = false
)

@Entity(
    tableName = "reflection_answers",
    indices = [Index(value = ["dateStr", "questionIndex"], unique = true)]
)
data class ReflectionAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String, // "YYYY-MM-DD"
    val questionIndex: Int,
    val questionText: String,
    val answerText: String,
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "self_reflections")
data class SelfReflectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sectionKey: String, // STRENGTHS, WEAKNESSES, MISTAKES, FEARS, LESSONS, EMOTIONS, ACHIEVEMENTS, IMPROVEMENTS, ASPIRATIONS, DISCIPLINE, VALUES
    val title: String,
    val content: String,
    val updatedAtEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "academic_subjects")
data class AcademicSubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val code: String = "",
    val colorHex: String = "#00B0FF",
    val targetWeeklyHours: Float = 10f,
    val completedTopics: Int = 0,
    val totalTopics: Int = 10
)

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val topic: String = "",
    val dateStr: String, // "YYYY-MM-DD"
    val durationMinutes: Int,
    val sessionType: String = "SELF_STUDY", // SELF_STUDY, LECTURE, REVISION, PRACTICE
    val notes: String = "",
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "academic_exams")
data class AcademicExamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val title: String,
    val dateStr: String,
    val isExam: Boolean = true, // true for exam, false for assignment
    val isCompleted: Boolean = false
)

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = "Calisthenics", // Gym, Calisthenics, Strength Training, Cardio, Walking, Running, Mobility, Flexibility, Custom
    val dateStr: String, // "YYYY-MM-DD"
    val durationMinutes: Int = 45,
    val exercisesSummary: String = "",
    val notes: String = "",
    val isPr: Boolean = false,
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "skill_progressions")
data class SkillProgressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val skillName: String, // Handstand, Muscle-up, Pull-ups, Push-ups, L-Sit, Pistol Squat
    val currentStage: Int = 1, // 1 to 5
    val maxStage: Int = 5,
    val stageTitle: String = "Stage 1: Foundation",
    val notes: String = "",
    val lastUpdatedStr: String = ""
)

@Entity(
    tableName = "health_records",
    indices = [Index(value = ["dateStr"], unique = true)]
)
data class HealthEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String, // "YYYY-MM-DD"
    val sleepHours: Float = 7.5f,
    val sleepQuality: Int = 4, // 1-5
    val hydrationMl: Int = 2500,
    val nutritionNotes: String = "Balanced protein & clean carbs",
    val recoveryScore: Int = 85, // 0-100
    val energyLevel: Int = 8, // 1-10
    val notes: String = "",
    val timestampEpochMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "journey_logs")
data class JourneyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateStr: String,
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val category: String, // Habits, Tasks, Goals, Academics, Fitness, Reflection, Evolution
    val actionType: String,
    val title: String,
    val description: String = "",
    val xpEarned: Int = 0
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Discipline",
    val type: String = "DAILY", // DAILY, WEEKLY, SPECIAL
    val xpReward: Int = 50,
    val isCompleted: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1,
    val deadlineStr: String = ""
)
