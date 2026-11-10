package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserProfileEntity::class,
        HabitEntity::class,
        HabitLogEntity::class,
        TaskEntity::class,
        GoalEntity::class,
        GoalProgressEntity::class,
        JournalEntity::class,
        ReflectionAnswerEntity::class,
        SelfReflectionEntity::class,
        AcademicSubjectEntity::class,
        StudySessionEntity::class,
        AcademicExamEntity::class,
        WorkoutEntity::class,
        SkillProgressEntity::class,
        HealthEntity::class,
        JourneyLogEntity::class,
        MissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun habitDao(): HabitDao
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
    abstract fun reflectionDao(): ReflectionDao
    abstract fun academicDao(): AcademicDao
    abstract fun fitnessDao(): FitnessDao
    abstract fun healthDao(): HealthDao
    abstract fun journeyDao(): JourneyDao
    abstract fun missionDao(): MissionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "samurai_os_rpg.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
