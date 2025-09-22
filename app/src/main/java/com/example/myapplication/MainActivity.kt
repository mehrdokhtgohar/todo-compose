package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import com.example.myapplication.data.ThemePreference
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.screens.TaskListScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue





class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = this // 👈 You already have the context here
            val isDarkMode by ThemePreference.isDarkMode(context).collectAsState(initial = false)

            MyApplicationTheme(darkTheme = isDarkMode) {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    TaskListScreen(paddingValues = paddingValues)
                }
            }
        }
    }
}



