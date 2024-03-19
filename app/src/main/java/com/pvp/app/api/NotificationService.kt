package com.pvp.app.api

import com.pvp.app.model.Notification

interface NotificationService {

    /**
     * Posts a notification to the android alarm manager.
     *
     * In case the notification id is not unique, the previous notification will be replaced.
     * If null id is provided, the notification will be posted with a random generated id.
     * If delay is provided, the notification will be posted after the delay.
     */
    fun post(
        notification: Notification
    )

    /**
     * Shows a notification to the user using the android notification manager.
     * The notification will be shown with the provided title.
     */
    fun show(
        notification: Notification,
        title: String
    )
}