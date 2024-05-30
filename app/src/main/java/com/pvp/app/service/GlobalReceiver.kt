package com.pvp.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pvp.app.api.NotificationService
import com.pvp.app.api.WorkService
import com.pvp.app.model.GlobalReceiverConstants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GlobalReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var workService: WorkService

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        when (val handler = intent?.getStringExtra(GlobalReceiverConstants.HANDLER_ID)) {
            NotificationService.BROADCAST_RECEIVER_HANDLER_ID -> {
                notificationService.processNotificationRequest(intent)
            }

            WorkService.BROADCAST_RECEIVER_HANDLER_ID -> {
                workService.processWorkerRequest(intent)
            }

            else -> throw IllegalArgumentException("Unknown handler id: $handler")
        }
    }
}