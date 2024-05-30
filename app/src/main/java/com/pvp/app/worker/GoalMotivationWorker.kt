package com.pvp.app.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.R
import com.pvp.app.api.FriendService
import com.pvp.app.api.GoalService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Notification
import com.pvp.app.model.NotificationChannel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate

@HiltWorker
class GoalMotivationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationService: NotificationService,
    private val userService: UserService,
    private val goalService: GoalService,
    private val friendService: FriendService
) : CoroutineWorker(
    context,
    workerParams
) {

    companion object {

        const val WORKER_NAME = "com.pvp.app.worker.GoalMotivationWorker"
    }

    override suspend fun doWork(): Result {
        val email = userService.user.first()?.email ?: return Result.failure()

        goalService
            .get(email)
            .first()
            .forEachIndexed { index, goal ->
                if (goal.completed || goal.endDate.isBefore(LocalDate.now())) {
                    return@forEachIndexed
                }

                val goalType = applicationContext.getString(goal.activity.titleId)

                val target = if (goal.steps) {
                    "${goal.target.toInt()} ${applicationContext.getString(R.string.measurement_steps)}"
                } else {
                    "${goal.target} ${applicationContext.getString(R.string.measurement_km)}"
                }

                val descriptions = if (goal.monthly) {
                    listOf(
                        R.string.worker_goal_motivation_notification_close_description_monthly,
                        R.string.worker_goal_motivation_notification_close_description_monthly_1,
                        R.string.worker_goal_motivation_notification_close_description_monthly_2
                    )
                } else {
                    listOf(
                        R.string.worker_goal_motivation_notification_close_description_weekly,
                        R.string.worker_goal_motivation_notification_close_description_monthly_1,
                        R.string.worker_goal_motivation_notification_close_description_monthly_2
                    )
                }

                val randomDescriptionId = descriptions.random()
                val description = applicationContext.getString(randomDescriptionId, goalType, target)

                val notification = Notification(
                    channel = NotificationChannel.GoalMotivation,
                    title = applicationContext.getString(R.string.worker_goal_motivation_notification_close_title),
                    text = description
                )

                notificationService.post(
                    notification = notification,
                    delay = Duration.ofMinutes((index + 1) * 30L)
                )
            }

        var hasActiveFriends = false

        run friends@{
            friendService
                .get(email)
                .first()
                ?.friends
                ?.forEach friendsForEach@{ friend ->
                    goalService
                        .get(friend.email)
                        .first()
                        .forEach goalsForEach@{ goal ->
                            if (goal.endDate.isBefore(LocalDate.now())) {
                                return@goalsForEach
                            }

                            if (goal.completed) {
                                hasActiveFriends = true
                                return@friends
                            }
                        }
                }
        }

        if (hasActiveFriends) {
            val notification = Notification(
                channel = NotificationChannel.GoalMotivation,
                title = applicationContext.getString(R.string.worker_goal_motivation_notification_buddies_title),
                text = applicationContext.getString(R.string.worker_goal_motivation_notification_buddies_description)
            )

            notificationService.post(
                notification = notification,
                delay = Duration.ofMinutes(10L)
            )
        }

        return Result.success()
    }
}