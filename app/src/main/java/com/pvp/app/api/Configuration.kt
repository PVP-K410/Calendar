package com.pvp.app.api

interface Configuration {

    /**
     * The number of daily tasks for the user
     */
    val dailyTaskCount: Int

    /**
     * Default decorations that are applied to the user avatar when no decorations are owned.
     */
    val defaultDecorationIds: List<String>

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
     * Cup volume values for the user to select from.
     */
    val rangeCupVolume: List<Int>

    /**
     * Height values for the user to select from.
     */
    val rangeHeight: List<Int>

    /**
     * Kilometers values for the user to select from.
     *
     * It should be used together with meters in some picker component. User could select the range
     * of kilometers provided in this list and also meters in range of 0-999.
     */
    val rangeKilometers: List<Int>

    /**
     * Mass values for the user to select from.
     */
    val rangeMass: List<Int>

    /**
     * Reminder minutes values for the user to select from.
     */
    val rangeReminderMinutes: List<Int>

    /**
     * The inclusive interval of hours during which water drinking reminders are being sent.
     * Represents a range from startHour to endHour.
     */
    val intervalDrinkReminder: Pair<Int, Int>

    /**
     * The inclusive interval of allowed username length
     */
    val intervalUsernameLength: Pair<Int, Int>
}