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

    fun toggleHabitCompletion(habit: HabitEntity, completed: Boolean){
        viewModelScope.launch {
            repository.updateHabitCompletionWithStreak(habit, completed)
        }
    }

    fun toggleHabitForDate(
        habit: HabitEntity,
        date: String,
        checked: Boolean
    ){
        viewModelScope.launch {
            if(checked){
                repository.markHabitDoneForDate(habit.id, date.toString())
            }else{
                repository.unmarkHabitForDate(habit.id,date.toString())
            }
            }
    }

    fun getCompletedHabitIdsForDate(date: String) =
        repository.getCompletedHabitIdsForDate(date)

    fun deleteHabit(habit: HabitEntity){
        viewModelScope.launch {
            repository.deleteHabit(habit.id)
        }
    }
}

