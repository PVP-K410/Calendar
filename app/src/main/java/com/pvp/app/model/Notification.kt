package com.pvp.app.model

data class Notification(
    var id: Int = 0,
    val channel: NotificationChannel,
    val title: String,
    val text: String,
) {
    init {
        if (id == 0) {
            id = hashCode()
        }
    }
}
