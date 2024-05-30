package com.pvp.app.service

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pvp.app.Activity
import com.pvp.app.R
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.model.GlobalReceiverConstants
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.Setting
import com.pvp.app.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

class NotificationServiceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val settingService: SettingService,
) : NotificationService {

    override fun post(notification: Notification) {
        if (notification.dateTime == null) {
            error("Notification dateTime must not be null")
        }

        postNotification(
            notification,
            notification.dateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
    }

    override fun post(
        notification: Notification,
        delay: Duration
    ) {
        postNotification(
            notification,
            System.currentTimeMillis() + delay.toMillis()
        )
    }

    override fun post(
        notification: Notification,
        dateTime: LocalDateTime
    ) {
        postNotification(
            notification,
            dateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
    }

    override fun post(
        notification: Notification,
        time: LocalTime
    ) {
        val dateTime = time.atDate(LocalDate.now())

        postNotification(
            notification,
            dateTime
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
    }

    override fun processNotificationRequest(intent: Intent) {
        val channelId = intent.getStringExtra(
            NotificationService.BROADCAST_RECEIVER_NOTIFICATION_CHANNEL_ID
        ) ?: ""

        val channel = NotificationChannel.fromChannelId(channelId)

        if (channel == NotificationChannel.Unknown) {
            error("Unknown notification channel id")
        }

        val id = intent.getIntExtra(
            NotificationService.BROADCAST_RECEIVER_NOTIFICATION_ID,
            0
        )

        val text = intent.getStringExtra(
            NotificationService.BROADCAST_RECEIVER_NOTIFICATION_TEXT
        ) ?: ""

        val title = intent.getStringExtra(
            NotificationService.BROADCAST_RECEIVER_NOTIFICATION_TITLE
        ) ?: ""

        show(
            notification = Notification(
                channel = channel,
                id = id,
                text = text,
                title = title
            )
        )
    }

    private fun postNotification(
        notification: Notification,
        triggerAtMillis: Long
    ) {
        if (triggerAtMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(
            context,
            GlobalReceiver::class.java
        )
            .apply {
                putExtra(
                    GlobalReceiverConstants.HANDLER_ID,
                    NotificationService.BROADCAST_RECEIVER_HANDLER_ID
                )

                putExtra(
                    NotificationService.BROADCAST_RECEIVER_NOTIFICATION_CHANNEL_ID,
                    notification.channel.channelId
                )

                putExtra(
                    NotificationService.BROADCAST_RECEIVER_NOTIFICATION_ID,
                    notification.id
                )

                putExtra(
                    NotificationService.BROADCAST_RECEIVER_NOTIFICATION_TEXT,
                    notification.text
                )

                putExtra(
                    NotificationService.BROADCAST_RECEIVER_NOTIFICATION_TITLE,
                    notification.title
                )
            }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notification.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    override fun show(notification: Notification) {
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent = Intent(
            context,
            Activity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val silent = with(LocalTime.now()) {
            this >= LocalTime.of(
                22,
                0
            ) || this <= LocalTime.of(
                8,
                0
            )
        }

        val notificationAndroid = NotificationCompat
            .Builder(
                context,
                notification.channel.channelId
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setContentText(notification.text)
            .setContentTitle(notification.title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSilent(silent)
            .setSmallIcon(R.drawable.logo)
            .setStyle(
                NotificationCompat
                    .BigTextStyle()
                    .bigText(notification.text)
            )
            .build()

        val manager = NotificationManagerCompat.from(context)

        manager.notify(
            notification.id,
            notificationAndroid
        )
    }

    override fun cancel(notification: Notification) {
        cancelNotification(id = notification.id)
    }

    override fun cancel(id: Int) {
        cancelNotification(id = id)
    }

    private fun cancelNotification(id: Int) {
        val intent = Intent(
            context,
            GlobalReceiver::class.java
        )
            .apply {
                putExtra(
                    GlobalReceiverConstants.HANDLER_ID,
                    NotificationService.BROADCAST_RECEIVER_HANDLER_ID
                )
            }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        pendingIntent?.let {
            val manager = context.getSystemService(AlarmManager::class.java)

            manager.cancel(it)

            it.cancel()
        }
    }

    override suspend fun getNotificationForTask(task: Task): Notification? {
        if (task.time == null || task.isCompleted) {
            return null
        }

        val reminderMinutes = task.reminderTime
            ?.toMinutes()
            ?: settingService
                .get(Setting.Notifications.ReminderBeforeTaskMinutes)
                .first()
                .toLong()

        val reminderDateTime = task.date
            .atTime(task.time)
            .minusMinutes(reminderMinutes)

        if (reminderDateTime.isBefore(LocalDateTime.now())) {
            return null
        }

        return Notification(
            channel = NotificationChannel.TaskReminder,
            title = context.getString(R.string.tasks_notification_reminder_title),
            text = context.getString(
                R.string.tasks_notification_reminder_description,
                task.title,
                reminderMinutes,
                context.getString(R.string.measurement_minutes)
            ),
            dateTime = reminderDateTime
        )
    }
}