package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.R
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.SportActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class WeeklyActivityWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthConnectService: HealthConnectService,
    private val exerciseService: ExerciseService,
    private val notificationService: NotificationService,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {

    override suspend fun doWork(): Result {
        return if (healthConnectService.permissionsGranted(PERMISSIONS)) {
            val user = userService.user.firstOrNull() ?: return Result.retry()
            val activities = getRandomInfrequentActivities()

            try {
                userService.merge(user.copy(weeklyActivities = activities))

                postActivityNotification(activities)

                Result.success()
            } catch (e: Exception) {
                Log.e(
                    WORKER_NAME,
                    "Failed to update ${user.email} weekly activities. Retrying...",
                    e
                )

                Result.retry()
            }
        } else {
            Result.failure()
        }
    }

    private fun formNotificationBody(activities: List<SportActivity>): String {
        return when (activities.count() == 1) {
            true -> {
                when (activities.contains(SportActivity.Wheelchair)) {
                    true -> {
                        applicationContext.getString(
                            R.string.worker_activity_notification_wheelchair_activities,
                            applicationContext.getString(SportActivity.Wheelchair.titleId)
                        )
                    }

                    else -> {
                        applicationContext.getString(
                            R.string.worker_activity_notification_single_activity,
                            applicationContext.getString(activities.first().titleId)
                        )
                    }
                }
            }

            else -> {
                applicationContext.getString(
                    R.string.worker_activity_notification_many,
                    activities
                        .dropLast(1)
                        .joinToString(separator = ", ") { applicationContext.getString(it.titleId) },
                    applicationContext.getString(activities.last().titleId)
                )
            }
        }
    }

    private suspend fun getRandomInfrequentActivities(count: Int = 4): List<SportActivity> {
        val activities = exerciseService
            .getInfrequentActivities()
            .toMutableList()

        val mostFrequentActivity = exerciseService.getMostFrequentActivity()

        return when (activities.contains(SportActivity.Wheelchair)) {
            true -> {
                activities.remove(SportActivity.Wheelchair)

                activities
                    .shuffled()
                    .take(count - 1)
                    .plus(mostFrequentActivity)
            }

            else -> listOf(SportActivity.Wheelchair)
        }
    }

    private fun postActivityNotification(activities: List<SportActivity>) {
        notificationService.show(
            Notification(
                channel = NotificationChannel.WeeklyActivityReminder,
                title = applicationContext.getString(R.string.worker_activity_notification_title),
                text = formNotificationBody(activities)
            )
        )
    }

    companion object {

        const val WORKER_NAME = "WeeklyActivityWorker"

        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class)
        )
    }
}