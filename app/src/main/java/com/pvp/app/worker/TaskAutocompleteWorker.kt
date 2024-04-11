package com.pvp.app.worker

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.service.TaskAutocompleteService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskAutocompleteWorker @AssistedInject constructor(
    @Assisted
    context: Context,
    @Assisted
    workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    companion object {

        const val WORKER_NAME = "TaskAutocompleteWorker"
    }

    override suspend fun doWork(): Result {
        Intent(
            applicationContext,
            TaskAutocompleteService::class.java
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