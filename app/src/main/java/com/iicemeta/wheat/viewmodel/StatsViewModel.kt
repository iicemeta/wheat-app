package com.iicemeta.wheat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iicemeta.wheat.data.database.AppDatabase
import com.iicemeta.wheat.util.computeHabit
import com.iicemeta.wheat.util.startOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DayExpense(val label: String, val amount: Double)

data class StatsUiState(
    val monthSpent: Double = 0.0,
    val savedPercent: Float = 1f,
    val last7Days: List<DayExpense> = emptyList(),
    val maxDayAmount: Double = 1.0,
    val streakDays: Int = 0,
    val monthLabel: String = ""
)

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val expenseDao = database.expenseDao()
    private val settingsDao = database.settingsDao()

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    private var budget: Double = 1500.0

    init {
        val monthFmt = SimpleDateFormat("yyyy年M月", Locale.CHINA)
        val weekFmt = SimpleDateFormat("E", Locale.CHINA)
        viewModelScope.launch {
            settingsDao.getSettings().collect { settings ->
                settings?.let {
                    budget = it.monthlyBudget
                    refreshSaved()
                }
            }
        }
        viewModelScope.launch {
            expenseDao.getAllExpenses().collect { expenses ->
                val now = System.currentTimeMillis()
                val habit = computeHabit(expenses.map { it.date }, now)

                val cal = Calendar.getInstance()
                cal.timeInMillis = now
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val monthStart = cal.timeInMillis
                val monthSpent = expenses
                    .filter { !it.isIncome && it.date >= monthStart && it.date <= now }
                    .sumOf { it.amount }

                // 近 7 天（含今天），每天支出合计
                val days = (6 downTo 0).map { offset ->
                    val c = Calendar.getInstance()
                    c.timeInMillis = now
                    c.add(Calendar.DAY_OF_YEAR, -offset)
                    val dayStart = startOfDay(c.timeInMillis)
                    val dayEnd = dayStart + 24L * 60 * 60 * 1000
                    val amount = expenses
                        .filter { !it.isIncome && it.date in dayStart until dayEnd }
                        .sumOf { it.amount }
                    val label = if (offset == 0) "今天" else weekFmt.format(c.time)
                    DayExpense(label, amount)
                }

                _uiState.value = _uiState.value.copy(
                    monthSpent = monthSpent,
                    last7Days = days,
                    maxDayAmount = (days.maxOfOrNull { it.amount } ?: 0.0).coerceAtLeast(1.0),
                    streakDays = habit.streakDays,
                    monthLabel = monthFmt.format(now)
                )
                refreshSaved()
            }
        }
    }

    private fun refreshSaved() {
        val state = _uiState.value
        val saved = if (budget > 0) {
            ((budget - state.monthSpent) / budget).toFloat().coerceIn(0f, 1f)
        } else 0f
        _uiState.value = state.copy(savedPercent = saved)
    }
}
