package com.example.habitcoachai.data.preferences

import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context : Context) {

    companion object{
        private val USER_NAME = stringPreferencesKey("user_name")
    }

    val userName: Flow<String> = context.dataStore.data
        .map {
            prefs -> prefs[USER_NAME] ?: ""
        }

    suspend fun saveUserName(name: String){
        context.dataStore.edit {
            prefs -> prefs[USER_NAME] = name
        }
    }
}