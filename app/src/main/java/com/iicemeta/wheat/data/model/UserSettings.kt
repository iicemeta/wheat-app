package com.iicemeta.wheat.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1,
    val monthlyBudget: Double = 2000.0,
    val currency: String = "¥",
    val username: String = ""
)
