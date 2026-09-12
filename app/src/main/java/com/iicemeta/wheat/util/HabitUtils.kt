package com.iicemeta.wheat.util

import com.iicemeta.wheat.viewmodel.WheatStage
import java.util.Calendar

/** 单日起点毫秒（本地时区）。 */
fun startOfDay(millis: Long): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = millis
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun isSameDay(a: Long, b: Long): Boolean = startOfDay(a) == startOfDay(b)

data class HabitInfo(
    val streakDays: Int = 0,
    val stage: WheatStage = WheatStage.SEED,
    /** 0f..1f：当前阶段内的成长进度 */
    val expPercent: Float = 0f,
    val todayCheckedIn: Boolean = false,
    /** 距下一阶段还差几天（丰收期为 0） */
    val daysToNext: Int = 1
)

/**
 * 习惯核心逻辑：小麦只因“坚持记账的天数”成长，与金额无关。
 * 阶段：0天=种子 / 1-2天=幼苗 / 3-6天=成长 / 7-13天=抽穗 / 14天+=丰收。
 * 今天尚未记账时，连击按昨天结算，保持鼓励不断签。
 */
fun computeHabit(recordDates: List<Long>, now: Long = System.currentTimeMillis()): HabitInfo {
    if (recordDates.isEmpty()) return HabitInfo()
    val days = recordDates.map { startOfDay(it) }.toSet()
    val today = startOfDay(now)
    val dayMs = 24L * 60 * 60 * 1000
    val todayCheckedIn = today in days

    var cursor = if (todayCheckedIn) today else today - dayMs
    var streak = 0
    while (cursor in days) {
        streak++
        cursor -= dayMs
    }

    val stage = when {
        streak >= 14 -> WheatStage.HARVEST
        streak >= 7 -> WheatStage.MATURE
        streak >= 3 -> WheatStage.GROWING
        streak >= 1 -> WheatStage.SPROUT
        else -> WheatStage.SEED
    }
    val (expPercent, daysToNext) = when (stage) {
        WheatStage.SEED -> 0f to 1
        WheatStage.SPROUT -> ((streak - 1) / 2f).coerceIn(0f, 1f) to (3 - streak)
        WheatStage.GROWING -> ((streak - 3) / 4f).coerceIn(0f, 1f) to (7 - streak)
        WheatStage.MATURE -> ((streak - 7) / 7f).coerceIn(0f, 1f) to (14 - streak)
        WheatStage.HARVEST -> 1f to 0
    }
    return HabitInfo(streak, stage, expPercent, todayCheckedIn, daysToNext)
}

fun stageTitle(stage: WheatStage): String = when (stage) {
    WheatStage.SEED -> "种子静待破土"
    WheatStage.SPROUT -> "幼苗破土而出"
    WheatStage.GROWING -> "麦苗茁壮成长"
    WheatStage.MATURE -> "麦穗正在抽穗"
    WheatStage.HARVEST -> "金色麦浪丰收"
}

fun stageTip(habit: HabitInfo): String = when (habit.stage) {
    WheatStage.SEED -> "今天记下第一笔，小麦就会发芽，不在多少，只在坚持。"
    WheatStage.SPROUT -> "已经坚持 ${habit.streakDays} 天，再坚持 ${habit.daysToNext} 天就能看到麦苗长高。"
    WheatStage.GROWING -> "坚持 ${habit.streakDays} 天，战胜遗忘就是胜利，再 ${habit.daysToNext} 天抽穗。"
    WheatStage.MATURE -> "坚持 ${habit.streakDays} 天，财务掌控力正在养成，再 ${habit.daysToNext} 天丰收。"
    WheatStage.HARVEST -> "连续 ${habit.streakDays} 天！你是真正的默默耕耘者，继续守护这片麦田。"
}
