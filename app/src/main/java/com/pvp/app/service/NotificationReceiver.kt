package com.pvp.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pvp.app.api.NotificationService
import com.pvp.app.model.Notification
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
        notificationService.show(
            notification = Notification(
                id = intent.getIntExtra(
                    "notificationId",
                    0
                ),
                channelId = intent.getStringExtra("notificationChannelId") ?: "",
                title = intent.getStringExtra("notificationTitle") ?: "",
                text = intent.getStringExtra("notificationText") ?: ""
            )
        )
    }
}