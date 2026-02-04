package com.todolist.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.todolist.R
import com.todolist.domain.model.Priority
import com.todolist.domain.model.Task
import com.todolist.ui.viewmodel.TaskViewModel
import java.util.*

/**
 * Screen for adding a new task or editing an existing task
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    viewModel: TaskViewModel,
    taskId: Long?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var task by remember { mutableStateOf<Task?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.LOW) }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val isEditMode = taskId != null && taskId > 0
    
    // Load task if editing
    LaunchedEffect(taskId) {
        if (taskId != null && taskId > 0) {
            // In a real scenario, you'd load from viewModel
            // For now, we'll handle it in the composable
        }
    }
    
    val titleError = title.isBlank()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (isEditMode) stringResource(R.string.edit_task)
                        else stringResource(R.string.new_task)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cancel)
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.task_title)) },
                placeholder = { Text(stringResource(R.string.task_title_hint)) },
                isError = titleError && title.isNotEmpty(),
                supportingText = {
                    if (titleError && title.isNotEmpty()) {
                        Text(stringResource(R.string.title_required))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.task_description)) },
                placeholder = { Text(stringResource(R.string.task_description_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )
            
            // Priority selection
            Text(
                text = stringResource(R.string.priority),
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedPriority == Priority.HIGH,
                    onClick = { selectedPriority = Priority.HIGH },
                    label = { Text("🔴 ${stringResource(R.string.priority_high)}") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedPriority == Priority.MEDIUM,
                    onClick = { selectedPriority = Priority.MEDIUM },
                    label = { Text("🟠 ${stringResource(R.string.priority_medium)}") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedPriority == Priority.LOW,
                    onClick = { selectedPriority = Priority.LOW },
                    label = { Text("🟢 ${stringResource(R.string.priority_low)}") },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Due date
            Text(
                text = stringResource(R.string.due_date_label),
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (dueDate != null) {
                            com.todolist.utils.DateUtils.formatDate(dueDate!!)
                        } else {
                            stringResource(R.string.select_date)
                        }
                    )
                }
                
                if (dueDate != null) {
                    OutlinedButton(
                        onClick = { dueDate = null }
                    ) {
                        Text(stringResource(R.string.clear_date))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Save button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        if (isEditMode && task != null) {
                            viewModel.updateTask(
                                task!!.copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    priority = selectedPriority,
                                    dueDate = dueDate
                                )
                            )
                        } else {
                            viewModel.addTask(
                                title = title.trim(),
                                description = description.trim(),
                                priority = selectedPriority,
                                dueDate = dueDate
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !titleError
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
    
    // Date picker dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate ?: System.currentTimeMillis()
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueDate = datePickerState.selectedDateMillis
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_task_title)) },
            text = { Text(stringResource(R.string.delete_task_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        task?.let { viewModel.deleteTask(it) }
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
