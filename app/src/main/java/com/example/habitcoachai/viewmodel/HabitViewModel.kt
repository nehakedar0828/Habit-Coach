package com.example.habitcoachai.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitcoachai.data.local.entity.HabitEntity
import com.example.habitcoachai.data.repository.HabitRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate




class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel(){

    val habits = repository.getHabits()
    private val _currentStreak = mutableStateOf(0)
    val currentStreak: State<Int> = _currentStreak


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
            loadCurrentStreak()
            }
    }

    fun getCompletedHabitIdsForDate(date: String) =
        repository.getCompletedHabitIdsForDate(date)

    fun deleteHabit(habit: HabitEntity){
        viewModelScope.launch {
            repository.deleteHabit(habit.id)
        }
    }

    fun loadCurrentStreak(){
        viewModelScope.launch {
            _currentStreak.value = repository.calculateCurrentStreak()
        }
    }

    fun completedDatesAsLocalDate(): Flow<Set<LocalDate>> {
        return completedDatesFlow().map { list ->
            list.map { LocalDate.parse(it) }.toSet()
        }
    }

    fun completedDatesFlow(): Flow<List<String>> {
        return repository.getAllCompletedDatesFlow()
    }

    fun weeklyCompletionStats() : Flow<Map<DayOfWeek, Int>> {
        return completedDatesAsLocalDate().map { completedDates ->

            val counts = DayOfWeek.values().associateWith { 0 }.toMutableMap()

            completedDates.forEach {
                date ->
                val day = date.dayOfWeek
                counts[day] = counts[day]!! + 1
            }

            counts
        }
    }

}

