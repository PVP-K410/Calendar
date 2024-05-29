package com.pvp.app.worker

import android.content.Context
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

    companion object {

        const val WORKER_NAME = "WeeklyActivityWorker"
    }

    override suspend fun doWork(): Result {
        val permissions = setOf(
            HealthPermission.getReadPermission(
                ExerciseSessionRecord::class
            )
        )

        return if (healthConnectService.permissionsGranted(permissions)) {
            val activities = getRandomInfrequentActivities()

            postActivityNotification(activities)

            userService.user
                .firstOrNull()
                ?.let { user ->
                    try {
                        userService.merge(
                            user.copy(
                                weeklyActivities = activities
                            )
                        )

                        Result.success()
                    } catch (e: Exception) {
                        Result.failure()
                    }
                } ?: Result.failure()
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
}