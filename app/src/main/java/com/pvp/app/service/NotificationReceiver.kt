package com.pvp.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pvp.app.api.NotificationService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        val id = intent.getIntExtra("notificationId", 0)
        val channelId = intent.getStringExtra("notificationChannelId") ?: ""
        val title = intent.getStringExtra("notificationTitle") ?: ""
        val text = intent.getStringExtra("notificationText") ?: ""

        val channel = NotificationChannel.fromChannelId(channelId)
        if (channel == NotificationChannel.UNKNOWN) {
            throw Exception("Unknown notification channel id")
        }

        notificationService.show(
            notification = Notification(
                id = id,
                channel = channel,
                title = title,
                text = text
            )
        )
    }
}