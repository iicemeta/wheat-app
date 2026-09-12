package com.iicemeta.wheat.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iicemeta.wheat.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getSettings(): Flow<UserSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: UserSettings)

    @Query("UPDATE user_settings SET monthlyBudget = :budget WHERE id = 1")
    suspend fun updateMonthlyBudget(budget: Double)

    @Query("UPDATE user_settings SET username = :username WHERE id = 1")
    suspend fun updateUsername(username: String)
}
