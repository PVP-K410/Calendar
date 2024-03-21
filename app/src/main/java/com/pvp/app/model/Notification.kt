package com.pvp.app.model

import java.time.Duration

data class Notification(
    val delay: Duration = Duration.ZERO,
    val id: Int? = null,
    val channelId: String = "No channelId provided",
    val title: String = "No title provided",
    val text: String = "No description provided"
)
