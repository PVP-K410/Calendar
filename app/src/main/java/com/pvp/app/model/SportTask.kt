package com.pvp.app.model

import java.time.Duration
import java.time.LocalDateTime

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
