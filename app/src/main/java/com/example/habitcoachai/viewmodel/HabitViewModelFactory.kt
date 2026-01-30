package com.example.habitcoachai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habitcoachai.data.local.db.HabitDatabase
import com.example.habitcoachai.data.repository.HabitRepository

class HabitViewModelFactory(
    private val database: HabitDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {

            val repository = HabitRepository(
                database.habitDao(),
                database.habitCompletionDao()
            )

            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
