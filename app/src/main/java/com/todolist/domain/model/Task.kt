package com.todolist.domain.model

import com.todolist.data.local.TaskEntity

/**
 * Domain model for a Task
 * This is the clean representation used in the UI and business logic
 */
data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.LOW,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Extension function to convert Task domain model to TaskEntity (database model)
 */
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        priority = priority.name,
        dueDate = dueDate,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}

/**
 * Extension function to convert TaskEntity (database model) to Task domain model
 */
fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = Priority.fromString(priority),
        dueDate = dueDate,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}
