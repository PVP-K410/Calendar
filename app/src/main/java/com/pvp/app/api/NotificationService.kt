package com.pvp.app.api

import com.pvp.app.model.Notification
import java.time.Duration
import java.time.LocalDateTime

interface NotificationService {

    /**
     * Posts a notification to the android alarm manager with the specified delay.
     */
    fun post(
        notification: Notification,
        delay: Duration
    )

    /**
     * Posts a notification to the android alarm manager at the specified date.
     */
    fun post(
        notification: Notification,
        dateTime: LocalDateTime
    )

    /**
     * Shows a notification to the user using the android notification manager.
     * The notification will be shown with the provided title.
     */
    fun show(
        notification: Notification
    )
}