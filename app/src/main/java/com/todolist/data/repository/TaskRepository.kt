package com.todolist.data.repository

import com.todolist.data.local.TaskDao
import com.todolist.domain.model.Task
import com.todolist.domain.model.toDomain
import com.todolist.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for task operations
 * Abstracts the data source and provides clean API for ViewModels
 */
class TaskRepository(private val taskDao: TaskDao) {
    
    /**
     * Get all tasks
     */
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Get a single task by ID
     */
    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)?.toDomain()
    }
    
    /**
     * Get active (incomplete) tasks
     */
    fun getActiveTasks(): Flow<List<Task>> {
        return taskDao.getActiveTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Get completed tasks
     */
    fun getCompletedTasks(): Flow<List<Task>> {
        return taskDao.getCompletedTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Get tasks sorted by due date
     */
    fun getTasksSortedByDueDate(): Flow<List<Task>> {
        return taskDao.getTasksSortedByDueDate().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Get tasks sorted by priority
     */
    fun getTasksSortedByPriority(): Flow<List<Task>> {
        return taskDao.getTasksSortedByPriority().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    /**
     * Insert a new task
     */
    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }
    
    /**
     * Update an existing task
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }
    
    /**
     * Delete a task
     */
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }
    
    /**
     * Toggle task completion status
     */
    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean) {
        taskDao.toggleTaskCompletion(taskId, isCompleted)
    }
}
