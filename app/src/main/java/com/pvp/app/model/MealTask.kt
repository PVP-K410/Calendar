package com.pvp.app.model

import java.time.Duration
import java.time.LocalDateTime

sealed class MealTask(
    description: String?,
    duration: Duration?,
    isCompleted: Boolean,
    var recipe: String,
    scheduledAt: LocalDateTime,
    title: String
) : Task(
    description,
    duration,
    isCompleted,
    scheduledAt,
    title
)
