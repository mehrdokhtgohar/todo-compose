package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit

val Context.themeDataStore by preferencesDataStore("theme_prefs")

object ThemePreference {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")

    fun isDarkMode(context: Context): Flow<Boolean> {
        return context.themeDataStore.data.map { prefs ->
            prefs[DARK_MODE_KEY] ?: false
        }
    }

    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.themeDataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }
}
