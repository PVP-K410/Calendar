package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.ExerciseService
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
        val activities = getRandomInfrequentActivities()

        postActivityNotification(activities)

        return userService.user
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
    }

    private fun formNotificationBody(activities: List<SportActivity>): String {
        return when (activities.count() == 1) {
            true -> {
                when (activities.contains(SportActivity.Wheelchair)) {
                    true -> {
                        "Participating in Wheelchair activities " +
                                "will give you more points this week!"
                    }

                    else -> {
                        "Participating in ${activities.first().title} " +
                                "will give you more points this week!"
                    }
                }
            }

            else -> {
                activities
                    .dropLast(1)
                    .joinToString(separator = ", ") { it.title } +
                        " and " + activities.last().title +
                        " will give you more points this week!"
            }
        }
    }

    private suspend fun getRandomInfrequentActivities(count: Int = 4): List<SportActivity> {
        val activities = exerciseService
            .getInfrequentActivities()
            .toMutableList()

        return when (activities.contains(SportActivity.Wheelchair)) {
            true -> {
                activities.remove(SportActivity.Wheelchair)

                activities
                    .shuffled()
                    .take(count)
            }

            else -> listOf(SportActivity.Wheelchair)
        }
    }

    private fun postActivityNotification(activities: List<SportActivity>) {
        notificationService.show(
            Notification(
                channel = NotificationChannel.WeeklyActivityReminder,
                title = "Weekly Activities",
                text = formNotificationBody(activities)
            )
        )
    }
}