package com.example

import com.example.data.model.HabitLogEntity
import com.example.ui.components.project3D
import com.example.ui.screens.calculateLongestStreak
import com.example.ui.screens.calculateStreak
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class ExampleUnitTest {
    private fun calculateJourneyWeekAndDay(startDateEpochMs: Long, now: Long): Pair<Int, Int> {
        val diffMs = (now - startDateEpochMs).coerceAtLeast(0L)
        val days = (diffMs / TimeUnit.DAYS.toMillis(1)).toInt() + 1
        val week = ((days - 1) / 7) + 1
        val dayInWeek = ((days - 1) % 7) + 1
        return Pair(week, dayInWeek)
    }

    private fun calculateRankTitle(week: Int): String {
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

    @Test
    fun testJourneyWeekAndDayCalculation() {
        val now = System.currentTimeMillis()
        val (week, day) = calculateJourneyWeekAndDay(now, now)
        assertEquals(1, week)
        assertEquals(1, day)

        // 8 days later -> Week 2, Day 2
        val eightDaysLaterStart = now - (8L * 24 * 60 * 60 * 1000)
        val (week2, day2) = calculateJourneyWeekAndDay(eightDaysLaterStart, now)
        assertEquals(2, week2)
        assertEquals(2, day2)
    }

    @Test
    fun testRankTitleProgression() {
        assertTrue(calculateRankTitle(1).contains("Mizunoto"))
        assertTrue(calculateRankTitle(12).contains("Kanoto"))
        assertTrue(calculateRankTitle(52).contains("Hashira"))
    }

    @Test
    fun testHabitStreakCalculations() {
        val today = "2026-11-10"
        val logs = listOf(
            HabitLogEntity(habitId = 1, dateStr = "2026-11-10", completed = true),
            HabitLogEntity(habitId = 1, dateStr = "2026-11-09", completed = true),
            HabitLogEntity(habitId = 1, dateStr = "2026-11-08", completed = true)
        )
        val streak = calculateStreak(logs, today)
        assertEquals(3, streak)

        val longest = calculateLongestStreak(setOf("2026-11-05", "2026-11-06", "2026-11-07", "2026-11-10"))
        assertEquals(3, longest)
    }

    @Test
    fun test3DProjection() {
        val pt = project3D(0f, 0f, 0f, 0f, 0f, 500f, 500f, 450f)
        assertNotNull(pt)
        assertEquals(500f, pt!!.x, 0.01f)
        assertEquals(500f, pt.y, 0.01f)
    }
}
