package com.iicemeta.wheat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iicemeta.wheat.data.database.AppDatabase
import com.iicemeta.wheat.data.model.Expense
import com.iicemeta.wheat.util.computeHabit
import com.iicemeta.wheat.util.startOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val streakDays: Int = 0,
    val wheatExp: Int = 0,
    val daysToNext: Int = 1,
    val todayCheckedIn: Boolean = false,
    val wheatStage: WheatStage = WheatStage.SEED,
    val todayRecords: List<Expense> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val expenseDao = database.expenseDao()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            expenseDao.getAllExpenses().collect { expenses ->
                val habit = computeHabit(expenses.map { it.date })
                val today = startOfDay(System.currentTimeMillis())
                val todayRecords = expenses.filter { startOfDay(it.date) == today }.take(20)
                _uiState.value = HomeUiState(
                    streakDays = habit.streakDays,
                    wheatExp = (habit.expPercent * 100).toInt(),
                    daysToNext = habit.daysToNext,
                    todayCheckedIn = habit.todayCheckedIn,
                    wheatStage = habit.stage,
                    todayRecords = todayRecords
                )
            }
        }
    }
}
