package com.todolist.domain.model

/**
 * Enum class representing task priority levels
 */
enum class Priority {
    HIGH,
    MEDIUM,
    LOW;

    companion object {
        fun fromString(value: String): Priority {
            return when (value.uppercase()) {
                "HIGH" -> HIGH
                "MEDIUM" -> MEDIUM
                "LOW" -> LOW
                else -> LOW
            }
        }
    }
}
