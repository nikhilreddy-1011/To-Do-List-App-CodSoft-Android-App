package com.todolist.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a task
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val priority: String, // Stored as string: "HIGH", "MEDIUM", "LOW"
    val dueDate: Long?, // Timestamp in milliseconds, nullable
    val isCompleted: Boolean,
    val createdAt: Long // Timestamp when task was created
)
