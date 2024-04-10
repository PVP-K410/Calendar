package com.pvp.app.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.service.TaskAutocompleteService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class TaskAutocompleteWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(
    context,
    workerParams
) {
    companion object {

        const val WORKER_NAME = "TaskAutocompleteWorker"
    }

    override suspend fun doWork(): Result {
        val context = applicationContext

        Intent(
            context,
            TaskAutocompleteService::class.java
        ).also { intent ->
            ContextCompat.startForegroundService(
                context,
                intent
            )
        }

        Log.e(
            "AUTOCOMPLETE",
            "FINISHED"
        )

        return Result.success()
    }
}