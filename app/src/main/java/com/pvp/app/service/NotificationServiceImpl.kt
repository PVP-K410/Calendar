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
import com.pvp.app.R
import com.pvp.app.api.NotificationService
import com.pvp.app.model.Notification
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject
import kotlin.random.Random
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class NotificationServiceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : NotificationService {

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
            dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }

    private fun postNotification(
        notification: Notification,
        triggerAtMillis: Long
    ) {
        val intent = Intent(
            context,
            NotificationReceiver::class.java
        )
            .apply {
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
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    override fun show(
        notification: Notification
    ) {
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificationAndroid = NotificationCompat.Builder(
            context,
            notification.channel.channelId
        )
            .setContentTitle(notification.title)
            .setContentText(notification.text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val manager = NotificationManagerCompat.from(context)

        manager.notify(
            Random.nextInt(),
            notificationAndroid
        )
    }
}