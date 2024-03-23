package com.pvp.app.worker

import android.content.Context
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

    companion object {

        const val WORKER_NAME = "TaskPointsDeductionWorker"
    }

    override suspend fun doWork(): Result {
        return userService.user
            .firstOrNull()
            ?.let {
                try {
                    pointService.deduct(
                        LocalDate
                            .now()
                            .minusDays(1)
                    )

                    Result.success()
                } catch (e: Exception) {
                    Result.failure()
                }
            }
            ?: Result.failure()
    }
}