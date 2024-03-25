package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.pvp.app.api.UserService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

@HiltWorker
class WeeklyActivityWorkerSetup @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val workManager: WorkManager,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {
    override suspend fun doWork(): Result {

        userService.user
            .firstOrNull()
            ?.let {
                if (it.increasedPointActivities.isEmpty()) {
                    workManager.enqueue(
                        OneTimeWorkRequestBuilder<WeeklyActivityWorker>()
                            .build()
                    )
                }
            }

        val requestPeriodic = PeriodicWorkRequestBuilder<WeeklyActivityWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WeeklyActivityWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            requestPeriodic
        )

        return Result.success()
    }
}