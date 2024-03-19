package com.pvp.app.model

import java.time.Duration

data class Notification(
    val delay: Duration = Duration.ZERO,
    val id: Int? = null,
    val text: String = "No description provided"
)
