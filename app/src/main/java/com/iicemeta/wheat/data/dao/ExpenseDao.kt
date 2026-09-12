package com.iicemeta.wheat.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iicemeta.wheat.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): Flow<List<Expense>>

    @Query("SELECT date FROM expenses ORDER BY date DESC")
    fun getAllRecordDates(): Flow<List<Long>>

    @Query("SELECT category AS category, SUM(amount) AS total FROM expenses WHERE isIncome = 0 AND date BETWEEN :startDate AND :endDate GROUP BY category ORDER BY total DESC")
    fun getCategoryTotals(startDate: Long, endDate: Long): Flow<List<CategoryTotal>>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 0 AND date BETWEEN :startDate AND :endDate")
    fun getTotalExpenseByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 1 AND date BETWEEN :startDate AND :endDate")
    fun getTotalIncomeByDateRange(startDate: Long, endDate: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE isIncome = 0")
    fun getTotalExpenseAllTime(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun countByDateRange(startDate: Long, endDate: Long): Int

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

data class CategoryTotal(
    val category: String,
    val total: Double
)
