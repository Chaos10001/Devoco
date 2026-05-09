package com.chaos.devoco.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences (private val context: Context){

    companion object{
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    val isFirstLaunch: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }

    suspend fun setDarkMode(enabled: Boolean){
        context.dataStore.edit{ preference ->
            preference[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setFirstLaunchCompleted(){
        context.dataStore.edit{ preference ->
            preference[FIRST_LAUNCH_KEY] = false
        }
    }
}