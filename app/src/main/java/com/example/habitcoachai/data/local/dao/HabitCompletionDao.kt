package com.example.habitcoachai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.habitcoachai.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(
        entity: HabitCompletionEntity)

    @Query("""
        SELECT habitId FROM habit_completions
        WHERE date = :date
    """)
    fun getCompletedHabitIdsForDate(date: String): Flow<List<Int>>

    @Query("""
        DELETE FROM habit_completions
        WHERE habitId = :habitId AND date = :date
    """)
    suspend fun deleteCompletionForDate(
        habitId: Int,
        date: String
    )

    @Query("""
        SELECT DISTINCT date FROM habit_completions
        ORDER BY date DESC
    """)
    suspend fun getAllCompletedDates(): List<String>

    @Query("""
        SELECT DISTINCT date FROM habit_completions
    """)
    fun getAllCompletedDatesFlow(): Flow<List<String>>
}