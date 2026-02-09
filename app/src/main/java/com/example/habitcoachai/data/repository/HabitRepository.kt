package com.example.habitcoachai.data.repository

import com.example.habitcoachai.data.local.dao.HabitCompletionDao
import com.example.habitcoachai.data.local.dao.HabitDao
import com.example.habitcoachai.data.local.entity.HabitCompletionEntity
import com.example.habitcoachai.data.local.entity.HabitEntity
import java.time.LocalDate

class HabitRepository(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao
) {

    fun getHabits() = habitDao.getAllHabits()

    suspend fun addHabit(name: String) {
        habitDao.insertHabit(
            HabitEntity(name = name)
        )
    }

    suspend fun updateHabitCompletion(
        habitId: Int,
        completed: Boolean
    ) {
        habitDao.updateHabitCompletion(habitId, completed)
    }

    suspend fun updateHabitCompletionWithStreak(
        habit: HabitEntity,
        completed: Boolean
    ) {
        val today = java.time.LocalDate.now()
        val todayStr = today.toString()

        val lastDate = habit.lastCompletedDate?.let {
            java.time.LocalDate.parse(it)
        }

        val newStreak = if (completed) {
            if (lastDate == today) {
                habit.streak
            } else if (lastDate != null && lastDate.plusDays(1) == today) {
                habit.streak + 1
            } else {
                1
            }
        } else {
            habit.streak
        }

        habitDao.updateHabitWithStreak(
            habitId = habit.id,
            completed = completed,
            streak = newStreak,
            date = if (completed) todayStr else habit.lastCompletedDate
        )
    }

    suspend fun markHabitDoneForDate(
        habitId: Int,
        date: String
    ) {
        completionDao.insertCompletion(
            HabitCompletionEntity(
                habitId = habitId,
                date = date
            )
        )
    }

    fun getCompletedHabitIdsForDate(date: String) =
        completionDao.getCompletedHabitIdsForDate(date)

    suspend fun deleteHabit(habitId: Int){
        habitDao.deleteHabit(habitId)
    }

    suspend fun unmarkHabitForDate(
        habitId: Int,
        date: String
    ) {
        completionDao.deleteCompletionForDate(
            habitId = habitId,
            date = date
        )
    }

    suspend fun calculateCurrentStreak(): Int{
        val dates = completionDao.getAllCompletedDates()
            .map{ LocalDate.parse(it)}

        if(dates.isEmpty()) return 0


        var streak = 0
        var expectedDate =LocalDate.now()

        for(date in dates) {
            if(date == expectedDate){
                streak++
                expectedDate = expectedDate.minusDays(1)
            }else if(date < expectedDate){
                break
            }
        }

        return streak
    }

    fun getAllCompletedDatesFlow() =
        completionDao.getAllCompletedDatesFlow()

}
