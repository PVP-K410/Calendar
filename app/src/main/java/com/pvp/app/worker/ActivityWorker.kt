package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.ActivityService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.ZoneId

@HiltWorker
class ActivityWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val activityService: ActivityService,
    private val healthConnectService: HealthConnectService,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {

    companion object {

        const val WORKER_NAME = "ActivityWorker"
    }

    override suspend fun doWork(): Result {
        val user = userService.user.firstOrNull() ?: return Result.failure()

        activityService.get(
            date = LocalDate.now(),
            email = user.email
        )
            .firstOrNull()
            ?.let { activity ->
                val calories = getCalories()
                val steps = getSteps()

                if (activity.calories < calories || activity.steps < steps) {
                    activityService.merge(
                        activity.copy(
                            calories = calories,
                            email = user.email,
                            steps = steps
                        )
                    )
                }
            }

        return Result.success()
    }

    private suspend fun getCalories(): Double {
        val date = LocalDate.now()

        val end = DateUtil.getEndInstant(date)

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateTotalCalories(
            start,
            end
        )
    }

    private suspend fun getSteps(): Long {
        val date = LocalDate.now()

        val end = date
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateSteps(
            start,
            end
        )
    }
}