package com.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.todolist.data.local.TaskDatabase
import com.todolist.data.repository.TaskRepository
import com.todolist.domain.model.Task
import com.todolist.ui.screens.AddEditTaskScreen
import com.todolist.ui.screens.HomeScreen
import com.todolist.ui.screens.SplashScreen
import com.todolist.ui.theme.ToDoListTheme
import com.todolist.ui.viewmodel.TaskViewModel
import com.todolist.ui.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database and repository
        val database = TaskDatabase.getDatabase(applicationContext)
        val repository = TaskRepository(database.taskDao())
        
        setContent {
            ToDoListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Simple navigation state - start with splash screen
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                    
                    val viewModel: TaskViewModel = viewModel(
                        factory = TaskViewModelFactory(repository)
                    )
                    
                    when (val screen = currentScreen) {
                        is Screen.Splash -> {
                            SplashScreen(
                                onSplashComplete = {
                                    currentScreen = Screen.Home
                                }
                            )
                        }
                        is Screen.Home -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToAddTask = {
                                    currentScreen = Screen.AddEdit(taskId = null)
                                },
                                onNavigateToEditTask = { task ->
                                    currentScreen = Screen.AddEdit(taskId = task.id)
                                }
                            )
                        }
                        is Screen.AddEdit -> {
                            AddEditTaskScreen(
                                viewModel = viewModel,
                                taskId = screen.taskId,
                                onNavigateBack = {
                                    currentScreen = Screen.Home
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simple sealed class for navigation
 */
sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    data class AddEdit(val taskId: Long?) : Screen()
}
