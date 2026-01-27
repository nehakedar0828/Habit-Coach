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
}