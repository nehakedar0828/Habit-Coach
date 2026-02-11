package com.example.habitcoachai.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitcoachai.data.local.entity.HabitEntity
import com.example.habitcoachai.data.repository.HabitRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


class HabitViewModel(
    private val repository: HabitRepository
) : ViewModel(){

    val habits = repository.getHabits()
    private val _currentStreak = mutableStateOf(0)
    val currentStreak: State<Int> = _currentStreak

    private val todayFlow = MutableStateFlow(LocalDate.now())


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

    fun toggleHabitForDate(//marks habit is done or not
        habit: HabitEntity,
        date: String,
        checked: Boolean
    ){
        viewModelScope.launch {

            val selectedDate = LocalDate.parse(date)
            val today = LocalDate.now()

            if(selectedDate.isAfter(today)) return@launch

            if(checked){
                repository.markHabitDoneForDate(habit.id, date.toString())
            }else{
                repository.unmarkHabitForDate(habit.id,date.toString())
            }
            loadCurrentStreak()
            refreshToday()
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
        return combine(
            completedDatesAsLocalDate(),
            todayFlow
        ) { completedDates, today ->

            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

            val thisWeekDates = completedDates.filter {
                !it.isBefore(startOfWeek) && !it.isAfter(today)
            }

            val counts = DayOfWeek.values().associateWith { 0 }.toMutableMap()

            thisWeekDates.forEach { date ->
                counts[date.dayOfWeek] = counts[date.dayOfWeek]!! + 1
            }

            counts
        }
    }

    fun refreshToday(){
        todayFlow.value = LocalDate.now()
    }

}

