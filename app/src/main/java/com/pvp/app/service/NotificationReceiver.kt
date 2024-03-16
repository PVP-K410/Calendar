package com.pvp.app.service

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pvp.app.R
import com.pvp.app.api.Configuration
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var configuration : Configuration

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        showNotification(
            context,
            intent.getStringExtra("notificationText"),
            intent.getIntExtra(
                "notificationId",
                0
            )
        )
    }

    private fun showNotification(
        context: Context,
        text: String?,
        notificationId: Int
    ) {
        val notificationManager = NotificationManagerCompat.from(context)

        val channel = NotificationChannel(
            configuration.channelNotificationTasksReminderId,
            "Task",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(
            context,
            configuration.channelNotificationTasksReminderId
        )
            .setContentTitle("Calendar reminder")
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(
            notificationId,
            notification
        )
    }
}

fun scheduleNotification(
    context: Context,
    text: String,
    delaySeconds: Int,
    notificationId: Int
) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(
        context,
        NotificationReceiver::class.java
    )

    intent.putExtra(
        "notificationText",
        text
    )
    intent.putExtra(
        "notificationId",
        notificationId
    )

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val triggerAtMillis = System.currentTimeMillis() + (delaySeconds * 1000)

    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        triggerAtMillis,
        pendingIntent
    )
}