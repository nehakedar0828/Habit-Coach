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

    fun weeklyStatsPerHabit() : Flow<Map<String, List<Int>>> {
        return combine(
            habits,
            todayFlow
        ) { habitList, today ->

            val startOfWeek = today.with(
                java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
            )

            val weekDates = (0..6).map { startOfWeek.plusDays(it.toLong()) }

            buildMap {
                habitList.forEach { habit ->

                    val counts = weekDates.map { date ->
                        repository.isHabitDoneOnDate(habit.id, date.toString())
                            .let { if (it) 1 else 0 }
                    }

                    put(habit.name, counts)
                }


            }
        }
    }

    fun completionRateLast30Days(): Flow<Int> {
        return combine(
            habits,
            completedDatesAsLocalDate()
        ) { habitList, completedDates ->

            if (habitList.isEmpty()) return@combine 0

            val today = LocalDate.now()
            val last30Days = (0..29).map { today.minusDays(it.toLong())}

            val totalPossibleCompletions = habitList.size * last30Days.size

            val actualCompletions = completedDates.count { it in last30Days}

            ((actualCompletions / totalPossibleCompletions.toFloat()) * 100).toInt()

        }
    }

    fun bestDayOfWeek(): Flow<String> {
        return completedDatesAsLocalDate().map { dates ->

            val counts = DayOfWeek.values().associateWith { 0 }.toMutableMap()

            dates.forEach { date ->
                counts[date.dayOfWeek] = counts[date.dayOfWeek]!! + 1
            }

            counts.maxByOrNull { it.value }?.key
                ?.name?.lowercase()?.replaceFirstChar{ it.uppercase() }
                ?: "N/A"
        }
    }

    fun totalCompletions() : Flow<Int>{
        return completedDatesAsLocalDate().map { it.size }
    }
}

