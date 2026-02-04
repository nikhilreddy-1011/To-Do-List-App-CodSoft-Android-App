package com.todolist.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Task operations
 */
@Dao
interface TaskDao {
    
    /**
     * Get all tasks ordered by creation date (newest first)
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get a single task by ID
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): TaskEntity?
    
    /**
     * Get all active (incomplete) tasks
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get all completed tasks
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>
    
    /**
     * Get all tasks sorted by due date (null dates at the end)
     */
    @Query("SELECT * FROM tasks ORDER BY CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC")
    fun getTasksSortedByDueDate(): Flow<List<TaskEntity>>
    
    /**
     * Get all tasks sorted by priority (HIGH -> MEDIUM -> LOW)
     */
    @Query("""
        SELECT * FROM tasks 
        ORDER BY 
            CASE priority 
                WHEN 'HIGH' THEN 1 
                WHEN 'MEDIUM' THEN 2 
                WHEN 'LOW' THEN 3 
                ELSE 4 
            END
    """)
    fun getTasksSortedByPriority(): Flow<List<TaskEntity>>
    
    /**
     * Insert a new task
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long
    
    /**
     * Update an existing task
     */
    @Update
    suspend fun updateTask(task: TaskEntity)
    
    /**
     * Delete a task
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)
    
    /**
     * Toggle task completion status
     */
    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun toggleTaskCompletion(taskId: Long, isCompleted: Boolean)
}
