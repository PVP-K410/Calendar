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
import javax.inject.Inject
import kotlin.random.Random

class NotificationServiceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
) : NotificationService {

    private fun generateId(): Int {
        return (0..Int.MAX_VALUE)
            .random(Random(System.currentTimeMillis()))
    }

    override fun post(
        notification: Notification
    ) {
        val id = notification.id ?: generateId()

        val intent = Intent(
            context,
            NotificationReceiver::class.java
        )
            .apply {
                putExtra(
                    "notificationId",
                    id
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

        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        manager.setExact(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + notification.delay.toMillis(),
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
            notification.id ?: 0,
            notificationAndroid
        )
    }
}