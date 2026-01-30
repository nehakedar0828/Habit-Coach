package com.example.habitcoachai.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "habit_completions",
    primaryKeys = ["habitId","date"]
    )
data class HabitCompletionEntity (
    val habitId: Int,
    val date: String,
    )