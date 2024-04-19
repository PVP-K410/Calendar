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

    private fun postNotification(
        notification: Notification,
        triggerAtMillis: Long
    ) {
        if (triggerAtMillis <= System.currentTimeMillis()) {
            return
        }

        val intent = Intent(
            context,
            NotificationReceiver::class.java
        )
            .apply {
                putExtra(
                    "notificationId",
                    notification.id
                )

                putExtra(
                    "notificationChannelId",
                    notification.channel.channelId
                )

                putExtra(
                    "notificationTitle",
                    notification.title
                )

                putExtra(
                    "notificationText",
                    notification.text
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

        val notificationAndroid = NotificationCompat.Builder(
            context,
            notification.channel.channelId
        )
            .setContentTitle(notification.title)
            .setContentText(notification.text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
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
            NotificationReceiver::class.java
        )

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        pendingIntent?.let {
            val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.cancel(it)
            it.cancel()
        }
    }

    override suspend fun getNotificationForTask(task: Task): Notification? {
        if (task.time == null || task.isCompleted) {
            return null
        }

        var reminderMinutes = settingService
            .get(Setting.Notifications.ReminderBeforeTaskMinutes)
            .first().toLong()

        if (task.reminderTime != null) {
            reminderMinutes = task.reminderTime?.toMinutes() ?: reminderMinutes
        }

        val reminderDateTime = task.date
            .atTime(task.time)
            .minusMinutes(reminderMinutes)

        if (reminderDateTime.isBefore(LocalDateTime.now())) {
            return null
        }

        return Notification(
            channel = NotificationChannel.TaskReminder,
            title = "Task Reminder",
            text = "Task '${task.title}' is in $reminderMinutes minute" +
                    "${if (reminderMinutes > 1) "s" else ""}...",
            dateTime = reminderDateTime
        )
    }
}