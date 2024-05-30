package com.pvp.app.worker

import android.content.Context
import android.content.Intent
import android.util.Log
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

    override suspend fun doWork(): Result {
        try {
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
        } catch (e: Exception) {
            Log.e(
                WORKER_NAME,
                "Failed to start ${AutocompleteService::class.java.simpleName}. Retrying...",
                e
            )

            return Result.retry()
        }

        return Result.success()
    }

    companion object {

        const val WORKER_NAME = "AutocompleteWorker"
    }
}