package com.example.habitcoachai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.habitcoachai.data.local.db.HabitDatabase
import com.example.habitcoachai.data.preferences.UserPreferences
import com.example.habitcoachai.data.repository.HabitRepository

class ViewModelFactory(
    private val database: HabitDatabase? = null,
    private val userPreferences: UserPreferences? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            requireNotNull(userPreferences) {
                "UserPreferences required for UserViewModel"
            }
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userPreferences) as T
        }

        if (modelClass.isAssignableFrom(HabitViewModel::class.java)) {
            requireNotNull(database) {
                "HabitDatabase required for HabitViewModel"
            }

            val repository = HabitRepository(
                habitDao = database.habitDao(),
                completionDao = database.habitCompletionDao()
            )

            @Suppress("UNCHECKED_CAST")
            return HabitViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
