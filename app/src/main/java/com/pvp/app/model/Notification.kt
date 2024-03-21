package com.pvp.app.model

import java.time.Duration

data class Notification(
    val delay: Duration = Duration.ZERO,
    val id: Int? = null,
    val channel: NotificationChannel,
    val title: String,
    val text: String
)
