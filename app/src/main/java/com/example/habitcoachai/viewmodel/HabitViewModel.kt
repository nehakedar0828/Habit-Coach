package com.example.habitcoachai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitcoachai.data.local.entity.HabitEntity
import com.example.habitcoachai.data.repository.HabitRepository
import kotlinx.coroutines.launch

class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel(){

    val habits = repository.getHabits()

    fun addHabit(name: String){
        viewModelScope.launch {
            repository.addHabit(name)
        }
    }

    fun toggleHabitCompletion(habitId: HabitEntity, completed: Boolean){
        viewModelScope.launch {
            repository.updateHabitCompletionWithStreak(habitId, completed)
        }
    }
}