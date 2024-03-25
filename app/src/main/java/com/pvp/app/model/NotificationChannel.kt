package com.pvp.app.model

enum class NotificationChannel(
    val channelId: String
) {

    Unknown("Unknown"),
    TaskReminder("Task Reminder"),
    DrinkReminder("Water Drinking Reminder"),
    IncreasedPointActivityReminder("Increased Point Activity Reminder");

    companion object {

        fun fromChannelId(channelId: String): NotificationChannel {
            return entries.find {
                it.channelId == channelId
            } ?: Unknown
        }
    }
}