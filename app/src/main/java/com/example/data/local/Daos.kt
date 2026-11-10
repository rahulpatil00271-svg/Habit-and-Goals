package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserProfileEntity)

    @Query("UPDATE user_profile SET totalXp = totalXp + :xpDelta WHERE id = 1")
    suspend fun addXp(xpDelta: Int)
}

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY priority DESC, id ASC")
    fun getAllActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits ORDER BY id ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun getHabitById(id: Long): Flow<HabitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY dateStr DESC")
    fun getLogsForHabit(habitId: Long): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs WHERE dateStr = :dateStr")
    fun getLogsForDate(dateStr: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM habit_logs ORDER BY dateStr DESC")
    fun getAllLogs(): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND dateStr = :dateStr")
    suspend fun deleteLog(habitId: Long, dateStr: String)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDateStr ASC, priority DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDateStr = :dateStr ORDER BY isCompleted ASC, priority DESC")
    fun getTasksForDate(dateStr: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY status ASC, deadlineStr ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    fun getGoalById(id: Long): Flow<GoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: GoalEntity): Long

    @Update
    suspend fun update(goal: GoalEntity)

    @Delete
    suspend fun delete(goal: GoalEntity)

    @Query("SELECT * FROM goal_progress_logs WHERE goalId = :goalId ORDER BY timestampEpochMs DESC")
    fun getProgressLogs(goalId: Long): Flow<List<GoalProgressEntity>>

    @Query("SELECT * FROM goal_progress_logs ORDER BY timestampEpochMs DESC")
    fun getAllProgressLogs(): Flow<List<GoalProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressLog(log: GoalProgressEntity)
}

@Dao
interface ReflectionDao {
    @Query("SELECT * FROM journals ORDER BY isPinned DESC, dateStr DESC, timestampEpochMs DESC")
    fun getAllJournals(): Flow<List<JournalEntity>>

    @Query("SELECT * FROM journals WHERE id = :id LIMIT 1")
    fun getJournalById(id: Long): Flow<JournalEntity?>

    @Query("SELECT * FROM journals WHERE dateStr = :dateStr ORDER BY timestampEpochMs DESC")
    fun getJournalsForDate(dateStr: String): Flow<List<JournalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournal(journal: JournalEntity): Long

    @Update
    suspend fun updateJournal(journal: JournalEntity)

    @Delete
    suspend fun deleteJournal(journal: JournalEntity)

    @Query("SELECT * FROM reflection_answers WHERE dateStr = :dateStr ORDER BY questionIndex ASC")
    fun getReflectionAnswersForDate(dateStr: String): Flow<List<ReflectionAnswerEntity>>

    @Query("SELECT * FROM reflection_answers ORDER BY dateStr DESC, questionIndex ASC")
    fun getAllReflectionAnswers(): Flow<List<ReflectionAnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAnswer(answer: ReflectionAnswerEntity)

    @Query("SELECT * FROM self_reflections ORDER BY id ASC")
    fun getAllSelfReflections(): Flow<List<SelfReflectionEntity>>

    @Query("SELECT * FROM self_reflections WHERE sectionKey = :key LIMIT 1")
    fun getSelfReflectionByKey(key: String): Flow<SelfReflectionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSelfReflection(item: SelfReflectionEntity)
}

@Dao
interface AcademicDao {
    @Query("SELECT * FROM academic_subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<AcademicSubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: AcademicSubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: AcademicSubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: AcademicSubjectEntity)

    @Query("SELECT * FROM study_sessions ORDER BY dateStr DESC, timestampEpochMs DESC")
    fun getAllStudySessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE dateStr = :dateStr ORDER BY timestampEpochMs DESC")
    fun getStudySessionsForDate(dateStr: String): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudySession(session: StudySessionEntity): Long

    @Delete
    suspend fun deleteStudySession(session: StudySessionEntity)

    @Query("SELECT * FROM academic_exams ORDER BY isCompleted ASC, dateStr ASC")
    fun getAllExams(): Flow<List<AcademicExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: AcademicExamEntity): Long

    @Update
    suspend fun updateExam(exam: AcademicExamEntity)

    @Delete
    suspend fun deleteExam(exam: AcademicExamEntity)
}

@Dao
interface FitnessDao {
    @Query("SELECT * FROM workouts ORDER BY dateStr DESC, timestampEpochMs DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE dateStr = :dateStr ORDER BY timestampEpochMs DESC")
    fun getWorkoutsForDate(dateStr: String): Flow<List<WorkoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM skill_progressions ORDER BY id ASC")
    fun getAllSkills(): Flow<List<SkillProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSkill(skill: SkillProgressEntity)
}

@Dao
interface HealthDao {
    @Query("SELECT * FROM health_records ORDER BY dateStr DESC")
    fun getAllHealthRecords(): Flow<List<HealthEntity>>

    @Query("SELECT * FROM health_records WHERE dateStr = :dateStr LIMIT 1")
    fun getHealthRecordForDate(dateStr: String): Flow<HealthEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHealth(record: HealthEntity)
}

@Dao
interface JourneyDao {
    @Query("SELECT * FROM journey_logs ORDER BY timestampEpochMs DESC")
    fun getAllLogs(): Flow<List<JourneyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: JourneyLogEntity): Long
}

@Dao
interface MissionDao {
    @Query("SELECT * FROM missions ORDER BY isCompleted ASC, type ASC")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: MissionEntity): Long

    @Update
    suspend fun updateMission(mission: MissionEntity)
}
