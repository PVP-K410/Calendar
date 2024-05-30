package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.PointService
import com.pvp.app.api.UserService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

@HiltWorker
class TaskPointsDeductionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val pointService: PointService,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        val user = userService.user.firstOrNull() ?: return Result.retry()

        return try {
            pointService.deduct(
                LocalDate
                    .now()
                    .minusDays(1)
            )

            Result.success()
        } catch (e: Exception) {
            Log.e(
                WORKER_NAME,
                "Failed to deduct task points for ${user.email}",
                e
            )

            Result.retry()
        }
    }

    companion object {

        const val WORKER_NAME = "TaskPointsDeductionWorker"
    }
}