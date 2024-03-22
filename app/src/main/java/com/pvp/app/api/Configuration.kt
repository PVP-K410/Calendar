package com.pvp.app.api

interface Configuration {

    /**
     * Task reminder ID for the notification channel. This id is then used in application settings.
     */
    val channelNotificationTasksReminderId: String

    /**
     * The Google OAuth client ID used for authentication.
     */
    val googleOAuthClientId: String

    /**
     * Mass values for the user to select from.
     */
    val rangeMass: List<Int>

    /**
     * Height values for the user to select from.
     */
    val rangeHeight: List<Int>
}