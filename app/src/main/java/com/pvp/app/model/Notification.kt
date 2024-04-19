package com.pvp.app.model

import java.time.LocalDateTime

data class Notification(
    var id: Int = 0,
    val channel: NotificationChannel,
    val title: String,
    val text: String,
    val dateTime: LocalDateTime? = null,
) {
    init {
        if (id == 0) {
            id = hashCode()
        }
    }
}
