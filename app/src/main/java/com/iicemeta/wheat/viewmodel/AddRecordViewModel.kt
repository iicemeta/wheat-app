package com.iicemeta.wheat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iicemeta.wheat.data.database.AppDatabase
import com.iicemeta.wheat.data.model.Expense
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AddRecordUiState(
    val amount: String = "",
    val category: String = "餐饮美食",
    val note: String = "",
    val isSaving: Boolean = false,
    val error: String? = null
)

/** 记账 BottomSheet 的状态：只记支出（学生记账场景），金额只影响粮仓，不影响小麦生长。 */
class AddRecordViewModel(application: Application) : AndroidViewModel(application) {
    private val expenseDao = AppDatabase.getDatabase(application).expenseDao()

    private val _uiState = MutableStateFlow(AddRecordUiState())
    val uiState: StateFlow<AddRecordUiState> = _uiState.asStateFlow()

    fun onAmountChanged(v: String) {
        _uiState.value = _uiState.value.copy(amount = v, error = null)
    }

    fun onCategoryChanged(v: String) {
        _uiState.value = _uiState.value.copy(category = v)
    }

    fun onNoteChanged(v: String) {
        _uiState.value = _uiState.value.copy(note = v)
    }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = state.copy(error = "请输入有效的金额")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null)
            expenseDao.insertExpense(
                Expense(
                    amount = amount,
                    category = state.category,
                    note = state.note.trim().ifEmpty { state.category }
                )
            )
            _uiState.value = AddRecordUiState()
            onSaved()
        }
    }

    fun reset() {
        _uiState.value = AddRecordUiState()
    }
}
