package com.example.habitcoachai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.example.habitcoachai.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Query("SELECT * FROM habits ORDER BY id DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("Update habits SET isCompleted = :completed WHERE id = :habitId")
    suspend fun updateHabitCompletion(
                 habitId: Int,
                 completed: Boolean
    )

}