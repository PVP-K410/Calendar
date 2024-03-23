package com.pvp.app.model

import kotlin.random.Random

data class Notification(
    val id: Int = Random.nextInt(),
    val channel: NotificationChannel,
    val title: String,
    val text: String
)
