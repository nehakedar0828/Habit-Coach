package com.example.habitcoachai.data.repository

import com.example.habitcoachai.data.local.dao.HabitDao
import com.example.habitcoachai.data.local.entity.HabitEntity

class HabitRepository(
    private val habitDao: HabitDao
) {

    fun getHabits() = habitDao.getAllHabits()

    suspend fun addHabit(name: String){
        habitDao.insertHabit(
            HabitEntity(name = name)
        )
    }

    suspend fun updateHabitCompletion(
        habitId: Int,
        completed: Boolean
    ){
        habitDao.updateHabitCompletion(habitId, completed)
    }

    suspend fun updateHabitCompletionWithStreak(
        habit: HabitEntity,
        completed: Boolean
    ){
        val today = java.time.LocalDate.now()
        val todayStr = today.toString()

        val lastDate = habit.lastCompletedDate?.let {
            java.time.LocalDate.parse(it)
        }

        val newStreak = if(completed) {
            if(lastDate == today){
                habit.streak
            }
            else if(lastDate != null && lastDate.plusDays(1) == today){
                habit.streak + 1
            }else{
                1
            }
        }else{
            habit.streak
        }

        habitDao.updateHabitWithStreak(
            habitId = habit.id,
            completed = completed,
            streak = newStreak,
            date = if(completed) todayStr else habit.lastCompletedDate
        )
    }
}