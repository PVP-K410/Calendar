package com.pvp.app.api

import android.content.Intent
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
    fun post(notification: Notification)

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
     * Processes a notification request from the broadcast receiver.
     */
    fun processNotificationRequest(intent: Intent)

    /**
     * Cancels a scheduled notification.
     * @param notification The notification to cancel.
     */
    fun cancel(notification: Notification)

    /**
     * Cancels a scheduled notification by its ID.
     * @param id The ID of the notification to cancel.
     */
    fun cancel(id: Int)

    /**
     * Shows a notification to the user using the android notification manager.
     * The notification will be shown with the provided title.
     */
    fun show(notification: Notification)

    /**
     * Converts task object to a notification object.
     */
    suspend fun getNotificationForTask(task: Task): Notification?

    companion object {

        /**
         * The value used to identify the broadcast receiver handler.
         * Value should be [String].
         */
        const val BROADCAST_RECEIVER_HANDLER_ID = "notification"

        /**
         * The key used to identify the broadcast receiver notification channel id.
         * Value should be [String].
         */
        const val BROADCAST_RECEIVER_NOTIFICATION_CHANNEL_ID = "notificationChannelId"

        /**
         * The key used to identify the broadcast receiver notification id.
         * Value should be [Int].
         */
        const val BROADCAST_RECEIVER_NOTIFICATION_ID = "notificationId"

        /**
         * The key used to identify the broadcast receiver notification text.
         * Value should be [String].
         */
        const val BROADCAST_RECEIVER_NOTIFICATION_TEXT = "notificationText"

        /**
         * The key used to identify the broadcast receiver notification title.
         * Value should be [String].
         */
        const val BROADCAST_RECEIVER_NOTIFICATION_TITLE = "notificationTitle"
    }
}