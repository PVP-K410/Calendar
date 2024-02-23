package com.pvp.app.model

import java.time.Duration
import java.time.LocalDateTime

sealed class Task(
    var description: String?,
    var duration: Duration?,
    var isCompleted: Boolean,
    var scheduledAt: LocalDateTime,
    var title: String
)