package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.example.myapplication.model.Task

val Context.taskDataStore by preferencesDataStore("tasks")

object TaskDataStore {
    private val TASKS_KEY = stringPreferencesKey("task_list")

    fun getTasks(context: Context): Flow<List<Task>> {
        return context.taskDataStore.data.map { preferences ->
            val json = preferences[TASKS_KEY] ?: return@map emptyList()
            try {
                Json.decodeFromString<List<Task>>(json)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun saveTasks(context: Context, tasks: List<Task>) {
        val json = Json.encodeToString(tasks)
        context.taskDataStore.edit { preferences ->
            preferences[TASKS_KEY] = json
        }
    }
}
