package com.pvp.app.model

enum class NotificationChannel(
    val channelId: String
) {

    DailyTaskReminder("Daily Task Reminder"),
    DrinkReminder("Water Drinking Reminder"),
    GoalMotivation("Goal Motivation"),
    TaskAutocomplete("Task Autocomplete"),
    TaskReminder("Task Reminder"),
    WeeklyActivityReminder("Weekly Activity Reminder"),

    Unknown("Unknown");

    companion object {

        fun fromChannelId(channelId: String): NotificationChannel {
            return entries.find {
                it.channelId == channelId
            } ?: Unknown
        }
    }
}