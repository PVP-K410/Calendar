package com.pvp.app.api

interface Configuration {

    /**
     * The Google OAuth client ID used for authentication.
     */
    val googleOAuthClientId: String

    /**
     * Limit of points that can be deducted from the user in a single day.
     */
    val limitPointsDeduction: Int

    /**
     * Limit of days within which the user can reclaim task points by marking the task as completed.
     * If the task is not marked as completed within this limit, the points are lost and never gain.
     */
    val limitPointsReclaimDays: Int

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