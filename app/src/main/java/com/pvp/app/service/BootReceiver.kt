package com.pvp.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pvp.app.api.WorkService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workService: WorkService

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        CoroutineScope(Dispatchers.IO)
            .launch {
                workService.handleWorkLogic()
            }
    }
}