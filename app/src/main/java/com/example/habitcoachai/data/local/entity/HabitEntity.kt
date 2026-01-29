package com.example.habitcoachai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val streak: Int = 0,
    val lastCompletedDate: String? = null
)
