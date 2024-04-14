package com.pvp.app.model

enum class NotificationChannel(
    val channelId: String
) {

    DailyTaskReminder("Daily Task Reminder"),
    DrinkReminder("Water Drinking Reminder"),
    TaskAutocomplete("Task Autocomplete"),
    TaskReminder("Task Reminder"),
    Unknown("Unknown"),
    WeeklyActivityReminder("Weekly Activity Reminder");

    companion object {

        fun fromChannelId(channelId: String): NotificationChannel {
            return entries.find {
                it.channelId == channelId
            } ?: Unknown
        }
    }
}