package com.todolist.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todolist.R
import com.todolist.domain.model.Task
import com.todolist.domain.model.TaskStatus
import com.todolist.ui.components.EmptyState
import com.todolist.ui.components.TaskCard
import com.todolist.ui.viewmodel.SortOrder
import com.todolist.ui.viewmodel.TaskViewModel

/**
 * Home screen showing list of tasks with filter and sort options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var showSortMenu by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Extract strings in composable context
    val taskDeletedMessage = stringResource(R.string.task_deleted)
    val undoLabel = stringResource(R.string.undo)
    
    // Show snackbar when task is deleted
    LaunchedEffect(uiState.recentlyDeletedTask) {
        uiState.recentlyDeletedTask?.let { deletedTask ->
            val result = snackbarHostState.showSnackbar(
                message = taskDeletedMessage,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearRecentlyDeleted()
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.sort_by)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_by_created)) },
                            onClick = {
                                viewModel.setSortOrder(SortOrder.CREATION_DATE)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_by_date)) },
                            onClick = {
                                viewModel.setSortOrder(SortOrder.DUE_DATE)
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.sort_by_priority)) },
                            onClick = {
                                viewModel.setSortOrder(SortOrder.PRIORITY)
                                showSortMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_task)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = uiState.filter == TaskStatus.ALL,
                    onClick = { viewModel.setFilter(TaskStatus.ALL) },
                    label = { Text(stringResource(R.string.all_tasks)) }
                )
                FilterChip(
                    selected = uiState.filter == TaskStatus.ACTIVE,
                    onClick = { viewModel.setFilter(TaskStatus.ACTIVE) },
                    label = { Text(stringResource(R.string.active_tasks)) }
                )
                FilterChip(
                    selected = uiState.filter == TaskStatus.COMPLETED,
                    onClick = { viewModel.setFilter(TaskStatus.COMPLETED) },
                    label = { Text(stringResource(R.string.completed_tasks)) }
                )
            }
            
            Divider()
            
            // Task list or empty state
            if (tasks.isEmpty()) {
                EmptyState(
                    filter = uiState.filter,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = tasks,
                        key = { it.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = {
                                viewModel.toggleTaskCompletion(task)
                            },
                            onClick = {
                                onNavigateToEditTask(task)
                            },
                            onDelete = {
                                taskToDelete = task
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text(stringResource(R.string.delete_task_title)) },
            text = { Text(stringResource(R.string.delete_task_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.let { viewModel.deleteTask(it) }
                        taskToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
