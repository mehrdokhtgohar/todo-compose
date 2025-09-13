// Task.kt
package com.example.myapplication.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val text: String,
    val isDone: Boolean = false
)