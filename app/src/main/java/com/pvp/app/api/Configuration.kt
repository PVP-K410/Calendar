package com.pvp.app.api

interface Configuration {

    /**
     * The Google OAuth client ID used for authentication.
     */
    val googleOAuthClientId: String

    /**
     * Height values for the user to select from.
     */
    val rangeHeight: List<Int>

    /**
     * Mass values for the user to select from.
     */
    val rangeMass: List<Int>

    /**
     * Reminder minutes values for the user to select from.
     */
    val rangeReminderMinutes: List<Int>

    /**
     * Cup volume values for the user to select from.
     */
    val rangeCupVolume: List<Int>

    /**
     * The inclusive interval of hours during which water drinking reminders are being sent.
     * Represents a range from startHour to endHour.
     */
    val intervalDrinkReminder: Pair<Int, Int>
}