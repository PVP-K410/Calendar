package com.pvp.app.worker

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.service.AutocompleteService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AutocompleteWorker @AssistedInject constructor(
    @Assisted
    context: Context,
    @Assisted
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    companion object {

        const val WORKER_NAME = "AutocompleteWorker"
    }

    override suspend fun doWork(): Result {
        Intent(
            applicationContext,
            AutocompleteService::class.java
        )
            .also { intent ->
                ContextCompat.startForegroundService(
                    applicationContext,
                    intent
                )
            }

        return Result.success()
    }
}