package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.TaskDataStore
import com.example.myapplication.model.Task
import com.example.myapplication.ui.components.TaskItem
import kotlinx.coroutines.launch

@Composable
fun TaskListScreen() {
    var taskText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val taskList = remember { mutableStateListOf<Task>() }

    // Load saved tasks once
    LaunchedEffect(Unit) {
        TaskDataStore.getTasks(context).collect { savedTasks ->
            taskList.clear()
            taskList.addAll(savedTasks)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("To-Do List", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            TextField(
                value = taskText,
                onValueChange = { taskText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter task") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (taskText.isNotBlank()) {
                        val newTask = Task(text = taskText)
                        taskList.add(newTask)
                        taskText = ""
                        scope.launch {
                            TaskDataStore.saveTasks(context, taskList)
                        }
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(taskList) { index, task ->
                TaskItem(
                    task = task,
                    onCheckedChange = { isChecked ->
                        val updatedTask = task.copy(isDone = isChecked)
                        taskList[index] = updatedTask

                        // Save on check change
                        scope.launch {
                            TaskDataStore.saveTasks(context, taskList)
                        }
                    },
                    onDeleteClick = {
                        taskList.removeAt(index)

                        // Save after delete
                        scope.launch {
                            TaskDataStore.saveTasks(context, taskList)
                        }
                    }
                )
            }
        }
    }
}
