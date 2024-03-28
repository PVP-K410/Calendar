package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyTaskWorkerSetup @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val workManager: WorkManager
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val request = PeriodicWorkRequestBuilder<DailyTaskWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            DailyTaskWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )

        return Result.success()
    }
}