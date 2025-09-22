package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.TaskDataStore
import com.example.myapplication.model.Task
import com.example.myapplication.ui.components.TaskItem
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.ThemePreference


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(paddingValues: PaddingValues) {
    var taskText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isDarkMode by ThemePreference.isDarkMode(context).collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val taskList = remember { mutableStateListOf<Task>() }
    val snackbarHostState = remember { SnackbarHostState() }
    var recentlyDeletedTask by remember { mutableStateOf<Task?>(null) }

    // Load saved tasks once
    LaunchedEffect(Unit) {
        TaskDataStore.getTasks(context).collect { savedTasks ->
            taskList.clear()
            taskList.addAll(savedTasks)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp) // your own padding
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dark Mode")
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = {
                        scope.launch {
                            ThemePreference.setDarkMode(context, it)
                        }
                    }
                )
            }
            Text(
                text = "📝 My Tasks",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row {
                TextField(
                    value = taskText,
                    onValueChange = { taskText = it },
                    placeholder = { Text("✍️ Add a new task...") },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { /* ... */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("➕ Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (taskList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 32.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🥱 Nothing to do!", style = MaterialTheme.typography.bodyLarge)
                    Text("Add your first task above ⬆️", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn {
                    itemsIndexed(taskList) { index, task ->
                        TaskItem(
                            task = task,
                            onCheckedChange = { isChecked ->
                                val updatedTask = task.copy(isDone = isChecked)
                                taskList[index] = updatedTask
                                scope.launch {
                                    TaskDataStore.saveTasks(context, taskList)
                                }
                            },
                            onDeleteClick = {
                                recentlyDeletedTask = task
                                taskList.removeAt(index)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Task deleted",
                                        actionLabel = "Undo"
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        recentlyDeletedTask?.let {
                                            taskList.add(index, it)
                                            recentlyDeletedTask = null
                                        }
                                    }
                                    TaskDataStore.saveTasks(context, taskList)
                                }
                            }
                        )
                    }
                }
            }

        }
    }
}

