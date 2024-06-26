package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.pvp.app.api.ActivityService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil
import com.pvp.app.common.DateUtil.toTimestamp
import com.pvp.app.model.ActivityEntry
import com.pvp.app.model.SportActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

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

    override suspend fun doWork(): Result {
        val user = userService.user.firstOrNull() ?: return Result.retry()

        user.lastActivitySync?.let { sync ->
            val difference = TimeUnit.HOURS.convert(
                Timestamp.now().seconds - sync.seconds,
                TimeUnit.SECONDS
            )

            if (difference < 2) {
                return Result.success()
            }
        }

        try {
            updateActivities(
                email = user.email,
                lastSync = user.lastActivitySync
                    ?: LocalDate
                        .now()
                        .minusDays(30)
                        .toTimestamp()
            )

            val hasDisability = checkForDisability(
                lastSync = user.lastActivitySync
                    ?: LocalDate
                        .now()
                        .minusDays(30)
                        .toTimestamp()
            )

            userService.merge(
                user.copy(
                    hasDisability = hasDisability,
                    lastActivitySync = Timestamp.now()
                )
            )
        } catch (e: Exception) {
            Log.e(
                this::class.simpleName,
                "Failed to update ${user.email} activities. Retrying...",
                e
            )

            return Result.retry()
        }

        return Result.success()
    }

    private suspend fun checkForDisability(lastSync: Timestamp): Boolean {
        val start = lastSync
            .toDate()
            .toInstant()

        val end = Instant.now()

        val activities = healthConnectService
            .readActivityData(
                ExerciseSessionRecord::class,
                start = start,
                end = end
            )
            .map { record ->
                SportActivity.fromId(record.exerciseType)
            }

        return activities.any { it == SportActivity.Wheelchair }
    }

    private suspend fun updateActivities(
        email: String,
        lastSync: Timestamp
    ) {
        val start = lastSync
            .toDate()
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val end = LocalDate.now()

        val dates = generateSequence(start) { it.plusDays(1) }
            .takeWhile { it <= end }
            .toList()

        for (date in dates) {
            val calories = getCalories(date)
            val distance = getDistance(date)
            val steps = getSteps(date)

            val activity = activityService
                .get(
                    date = date,
                    email = email
                )
                .firstOrNull()

            if (activity == null) {
                activityService.merge(
                    ActivityEntry(
                        date = date.toTimestamp(),
                        distance = distance,
                        calories = calories,
                        steps = steps,
                        email = email
                    )
                )
            } else {
                activityService.merge(
                    activity.copy(
                        calories = calories,
                        distance = distance,
                        steps = steps
                    )
                )
            }
        }
    }

    private suspend fun getCalories(date: LocalDate): Double {
        val end = DateUtil.toNowOrNextDay(date)

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateTotalCalories(
            start,
            end
        )
    }

    private suspend fun getDistance(date: LocalDate): Double {
        val end = date
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        val start = date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return healthConnectService.aggregateDistance(
            start,
            end
        )
    }

    private suspend fun getSteps(date: LocalDate): Long {
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