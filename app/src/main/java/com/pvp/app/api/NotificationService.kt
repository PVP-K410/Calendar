package com.pvp.app.api

import com.pvp.app.model.Notification
import com.pvp.app.model.Task
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

interface NotificationService {

    /**
     * Posts a notification to the android alarm manager.
     * Notification object must include dateTime at which the notification will be posted.
     */
    fun post(
        notification: Notification
    )

    /**
     * Posts a notification to the android alarm manager with the specified delay.
     */
    fun post(
        notification: Notification,
        delay: Duration
    )

    /**
     * Posts a notification to the android alarm manager at the specified date time.
     */
    fun post(
        notification: Notification,
        dateTime: LocalDateTime
    )

    /**
     * Posts a notification to the android alarm manager at the specified time.
     */
    fun post(
        notification: Notification,
        time: LocalTime
    )

    /**
     * Cancels a scheduled notification.
     * @param notification The notification to cancel.
     */
    fun cancel(
        notification: Notification
    )

    /**
     * Cancels a scheduled notification by its ID.
     * @param id The ID of the notification to cancel.
     */
    fun cancel(
        id: Int
    )

    /**
     * Shows a notification to the user using the android notification manager.
     * The notification will be shown with the provided title.
     */
    fun show(
        notification: Notification
    )

    /**
     * Converts task object to a notification object.
     */
    suspend fun getNotificationForTask(
        task: Task
    ): Notification?
}