package com.todolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.todolist.data.repository.TaskRepository
import com.todolist.domain.model.Priority
import com.todolist.domain.model.Task
import com.todolist.domain.model.TaskStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Enum for sort order
 */
enum class SortOrder {
    CREATION_DATE,
    DUE_DATE,
    PRIORITY
}

/**
 * UI State for the task list
 */
data class TaskUiState(
    val filter: TaskStatus = TaskStatus.ALL,
    val sortOrder: SortOrder = SortOrder.CREATION_DATE,
    val recentlyDeletedTask: Task? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for managing tasks
 */
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    
    // UI state
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    // Tasks flow based on filter and sort
    val tasks: StateFlow<List<Task>> = combine(
        _uiState.map { it.filter },
        _uiState.map { it.sortOrder }
    ) { filter, sortOrder ->
        filter to sortOrder
    }.flatMapLatest { (filter, sortOrder) ->
        getTasksFlow(filter, sortOrder)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    /**
     * Get appropriate task flow based on filter and sort
     */
    private fun getTasksFlow(filter: TaskStatus, sortOrder: SortOrder): Flow<List<Task>> {
        // First get filtered tasks
        val filteredFlow = when (filter) {
            TaskStatus.ALL -> repository.getAllTasks()
            TaskStatus.ACTIVE -> repository.getActiveTasks()
            TaskStatus.COMPLETED -> repository.getCompletedTasks()
        }
        
        // Then apply sorting if not using creation date (default from DB)
        return when (sortOrder) {
            SortOrder.CREATION_DATE -> filteredFlow
            SortOrder.DUE_DATE -> filteredFlow.map { tasks ->
                tasks.sortedWith(compareBy(
                    { it.dueDate == null }, // null dates last
                    { it.dueDate }
                ))
            }
            SortOrder.PRIORITY -> filteredFlow.map { tasks ->
                tasks.sortedBy { it.priority }
            }
        }
    }
    
    /**
     * Add a new task
     */
    fun addTask(
        title: String,
        description: String = "",
        priority: Priority = Priority.LOW,
        dueDate: Long? = null
    ) {
        if (title.isBlank()) return
        
        viewModelScope.launch {
            val task = Task(
                title = title.trim(),
                description = description.trim(),
                priority = priority,
                dueDate = dueDate,
                isCompleted = false
            )
            repository.insertTask(task)
        }
    }
    
    /**
     * Update an existing task
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
    
    /**
     * Delete a task
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _uiState.update { it.copy(recentlyDeletedTask = task) }
            repository.deleteTask(task)
        }
    }
    
    /**
     * Undo the last deletion
     */
    fun undoDelete() {
        viewModelScope.launch {
            _uiState.value.recentlyDeletedTask?.let { task ->
                repository.insertTask(task)
                _uiState.update { it.copy(recentlyDeletedTask = null) }
            }
        }
    }
    
    /**
     * Clear the recently deleted task (after snackbar dismisses)
     */
    fun clearRecentlyDeleted() {
        _uiState.update { it.copy(recentlyDeletedTask = null) }
    }
    
    /**
     * Toggle task completion status
     */
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task.id, !task.isCompleted)
        }
    }
    
    /**
     * Set filter (All, Active, Completed)
     */
    fun setFilter(filter: TaskStatus) {
        _uiState.update { it.copy(filter = filter) }
    }
    
    /**
     * Set sort order
     */
    fun setSortOrder(sortOrder: SortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }
}

/**
 * Factory for creating TaskViewModel with repository
 */
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
