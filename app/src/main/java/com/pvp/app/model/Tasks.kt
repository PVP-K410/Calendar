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

sealed class SportTask(
    var activity: SportActivity,
    description: String?,
    var distance: Double?,
    duration: Duration?,
    isCompleted: Boolean,
    scheduledAt: LocalDateTime,
    title: String
) : Task(
    description,
    duration,
    isCompleted,
    scheduledAt,
    title
)

sealed class Task(
    var description: String?,
    var duration: Duration?,
    var isCompleted: Boolean,
    var scheduledAt: LocalDateTime,
    var title: String
)
