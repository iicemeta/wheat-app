package com.iicemeta.wheat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iicemeta.wheat.data.database.AppDatabase
import com.iicemeta.wheat.util.startOfDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt

data class CategoryShare(
    val category: String,
    val amount: Double,
    val percent: Float
)

data class GranaryUiState(
    val monthlyBudget: Double = 1500.0,
    val totalSpent: Double = 0.0,
    val remainingPercent: Float = 1f,
    val dailyAverage: Double = 0.0,
    val dayOfMonth: Int = 1,
    val shares: List<CategoryShare> = emptyList(),
    val isEditingBudget: Boolean = false,
    val budgetInput: String = "1500"
)

class GranaryViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val expenseDao = database.expenseDao()
    private val settingsDao = database.settingsDao()

    private val _uiState = MutableStateFlow(GranaryUiState())
    val uiState: StateFlow<GranaryUiState> = _uiState.asStateFlow()

    private var budget: Double = 1500.0

    init {
        viewModelScope.launch {
            settingsDao.getSettings().collect { settings ->
                settings?.let {
                    budget = it.monthlyBudget
                    _uiState.value = _uiState.value.copy(
                        monthlyBudget = it.monthlyBudget,
                        budgetInput = it.monthlyBudget.roundToInt().toString()
                    )
                    refreshDerived()
                }
            }
        }
        viewModelScope.launch {
            val (start, end) = monthRange()
            expenseDao.getExpensesByDateRange(start, end).collect { expenses ->
                val spent = expenses.filter { !it.isIncome }.sumOf { it.amount }
                val byCat = expenses.filter { !it.isIncome }
                    .groupBy { it.category }
                    .mapValues { (_, v) -> v.sumOf { it.amount } }
                    .toList()
                    .sortedByDescending { it.second }
                val shares = byCat.map { (cat, amount) ->
                    CategoryShare(
                        category = cat,
                        amount = amount,
                        percent = if (spent > 0) (amount / spent).toFloat() else 0f
                    )
                }
                val cal = Calendar.getInstance()
                val day = cal.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
                _uiState.value = _uiState.value.copy(
                    totalSpent = spent,
                    shares = shares,
                    dailyAverage = spent / day,
                    dayOfMonth = day
                )
                refreshDerived()
            }
        }
    }

    private fun refreshDerived() {
        val state = _uiState.value
        val remaining = (budget - state.totalSpent).coerceAtLeast(0.0)
        _uiState.value = state.copy(
            remainingPercent = if (budget > 0) (remaining / budget).toFloat().coerceIn(0f, 1f) else 0f
        )
    }

    private fun monthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        return start to cal.timeInMillis
    }

    fun onBudgetInputChanged(input: String) {
        _uiState.value = _uiState.value.copy(budgetInput = input)
    }

    fun saveBudget() {
        val value = _uiState.value.budgetInput.toDoubleOrNull() ?: return
        if (value <= 0) return
        viewModelScope.launch {
            settingsDao.updateMonthlyBudget(value)
            _uiState.value = _uiState.value.copy(isEditingBudget = false)
        }
    }

    fun toggleEditBudget() {
        _uiState.value = _uiState.value.copy(
            isEditingBudget = !_uiState.value.isEditingBudget,
            budgetInput = _uiState.value.monthlyBudget.roundToInt().toString()
        )
    }

    fun granaryStatus(percent: Float): String = when {
        percent > 0.6f -> "储粮丰厚，安心食用"
        percent > 0.3f -> "存粮过半，注意节约"
        percent > 0.1f -> "粮仓告急，精打细算"
        else -> "空空如也，等待月底补给"
    }
}
