package com.example.habitcoachai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "habit_completions",
    primaryKeys = ["habitId","date"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
    )
data class HabitCompletionEntity (
    val habitId: Int,
    val date: String,
    )