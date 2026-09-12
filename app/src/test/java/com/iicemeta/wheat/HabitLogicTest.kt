package com.iicemeta.wheat

import com.iicemeta.wheat.util.computeHabit
import com.iicemeta.wheat.viewmodel.WheatStage
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class HabitLogicTest {
    private fun daysAgo(n: Int): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -n)
        return cal.timeInMillis
    }

    @Test
    fun emptyRecords_seedStage_zeroStreak() {
        val habit = computeHabit(emptyList())
        assertEquals(WheatStage.SEED, habit.stage)
        assertEquals(0, habit.streakDays)
    }

    @Test
    fun todayOnly_sproutStage_noAmountDependence() {
        // 金额无论大小都不应影响阶段：只看“今天是否记账”
        val habit = computeHabit(listOf(System.currentTimeMillis()))
        assertEquals(WheatStage.SPROUT, habit.stage)
        assertEquals(1, habit.streakDays)
        assertEquals(true, habit.todayCheckedIn)
    }

    @Test
    fun threeConsecutiveDays_growingStage() {
        val habit = computeHabit(listOf(daysAgo(0), daysAgo(1), daysAgo(2)))
        assertEquals(WheatStage.GROWING, habit.stage)
        assertEquals(3, habit.streakDays)
    }

    @Test
    fun gapBreaksStreak_countsOnlyConsecutiveTail() {
        // 今天 + 昨天 + 5天前（中间断了）：连击只算今天起连续的 2 天
        val habit = computeHabit(listOf(daysAgo(0), daysAgo(1), daysAgo(5)))
        assertEquals(2, habit.streakDays)
        assertEquals(WheatStage.SPROUT, habit.stage)
    }
}
