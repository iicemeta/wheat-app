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
import java.util.Calendar

data class MineUiState(
    val streakDays: Int = 0,
    val totalCheckInDays: Int = 0,
    val monthRecordCount: Int = 0,
    val monthlyBudget: Double = 1500.0
)

class MineViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val expenseDao = database.expenseDao()
    private val settingsDao = database.settingsDao()

    private val _uiState = MutableStateFlow(MineUiState())
    val uiState: StateFlow<MineUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDao.getSettings().collect { settings ->
                settings?.let {
                    _uiState.value = _uiState.value.copy(monthlyBudget = it.monthlyBudget)
                }
            }
        }
        viewModelScope.launch {
            expenseDao.getAllExpenses().collect { expenses ->
                val habit = computeHabit(expenses.map { it.date })
                val distinctDays = expenses.map { startOfDay(it.date) }.toSet().size
                val cal = Calendar.getInstance()
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val monthStart = cal.timeInMillis
                val monthCount = expenses.count { it.date >= monthStart }
                _uiState.value = _uiState.value.copy(
                    streakDays = habit.streakDays,
                    totalCheckInDays = distinctDays,
                    monthRecordCount = monthCount
                )
            }
        }
    }

    fun saveBudget(value: Double) {
        if (value <= 0) return
        viewModelScope.launch {
            settingsDao.updateMonthlyBudget(value)
        }
    }
}
