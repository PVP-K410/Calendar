package com.pvp.app.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.pvp.app.api.WorkService
import com.pvp.app.common.DateUtil.toEpochSecondTimeZoned
import com.pvp.app.model.NotificationChannel
import com.pvp.app.worker.ActivityWorker
import com.pvp.app.worker.AutocompleteWorker
import com.pvp.app.worker.DailyTaskWorker
import com.pvp.app.worker.DrinkReminderWorker
import com.pvp.app.worker.GoalMotivationWorker
import com.pvp.app.worker.MealPlanWorker
import com.pvp.app.worker.TaskNotificationWorker
import com.pvp.app.worker.TaskPointsDeductionWorker
import com.pvp.app.worker.WeeklyActivityWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) : WorkService {

    override fun createNotificationChannels() {
        val manager = NotificationManagerCompat.from(context)

        NotificationChannel.entries
            .filter { it != NotificationChannel.Unknown }
            .forEach { channel ->
                manager.createNotificationChannel(
                    android.app.NotificationChannel(
                        channel.channelId,
                        channel.channelId,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
            }
    }

    override fun initiateActivityWorker() {
        val preferences = context.getSharedPreferences(
            "ActivityWorker",
            Context.MODE_PRIVATE
        )

        val lastExecuted = preferences.getLong(
            "LastExecutionTime",
            0
        )

        val now = System.currentTimeMillis()

        if ((now - lastExecuted) > TimeUnit.HOURS.toMillis(2)) {
            workManager.enqueueUniqueWork(
                ActivityWorker.WORKER_NAME,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest<ActivityWorker>()
            )

            preferences
                .edit()
                .putLong(
                    "LastExecutionTime",
                    now
                )
                .apply()
        }
    }

    override fun initiateAutocompleteWorker() {
        workManager.enqueueUniquePeriodicWork(
            AutocompleteWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<AutocompleteWorker>(
                interval = 15,
                timeUnit = TimeUnit.MINUTES
            )
        )
    }

    override fun initiateDailyTaskWorker() {
        workManager.enqueueUniqueWork(
            DailyTaskWorker.WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest<DailyTaskWorker>()
        )

        val now = LocalDateTime.now()

        val target = now
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay()

        workManager.enqueueUniquePeriodicWork(
            DailyTaskWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<DailyTaskWorker>(
                delay = Duration.of(
                    target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned(),
                    ChronoUnit.SECONDS
                ),
                interval = 1,
                timeUnit = TimeUnit.DAYS
            )
        )
    }

    override fun initiateDrinkReminderWorker() {
        workManager.enqueueUniquePeriodicWork(
            DrinkReminderWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<DrinkReminderWorker>(
                interval = 1,
                timeUnit = TimeUnit.DAYS
            )
        )
    }

    override fun initiateGoalMotivationWorker() {
        workManager.enqueueUniquePeriodicWork(
            GoalMotivationWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<GoalMotivationWorker>(
                interval = 1,
                timeUnit = TimeUnit.DAYS
            )
        )
    }

    override fun initiateMealPlanWorker() {
        workManager.enqueueUniqueWork(
            MealPlanWorker.WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest<MealPlanWorker>()
        )

        val daysDelay = LocalDate
            .now().dayOfWeek.value
            .let { dayOfWeek ->
                if (dayOfWeek == 1) {
                    0
                } else {
                    8 - dayOfWeek
                }
            }

        workManager.enqueueUniquePeriodicWork(
            MealPlanWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<MealPlanWorker>(
                delay = Duration.of(
                    daysDelay.toLong(),
                    ChronoUnit.DAYS
                ),
                interval = 7,
                timeUnit = TimeUnit.DAYS
            )
        )
    }

    override fun initiateTaskNotificationWorker() {
        workManager.enqueueUniquePeriodicWork(
            TaskNotificationWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<TaskNotificationWorker>(
                interval = 1,
                timeUnit = TimeUnit.DAYS
            )
        )
    }

    override fun initiateTaskPointsDeductionWorker() {
        workManager.enqueueUniqueWork(
            TaskPointsDeductionWorker.WORKER_NAME,
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest<TaskPointsDeductionWorker>()
        )

        val now = LocalDateTime.now()

        val target = now
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay()

        workManager.enqueueUniquePeriodicWork(
            TaskPointsDeductionWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<TaskPointsDeductionWorker>(
                delay = Duration.of(
                    target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned(),
                    ChronoUnit.SECONDS
                ),
                interval = 1,
                timeUnit = TimeUnit.DAYS
            )
        )
    }

    override fun initiateWeeklyActivitiesWorker() {
        workManager.enqueueUniquePeriodicWork(
            WeeklyActivityWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicRequest<WeeklyActivityWorker>(
                interval = 7,
                timeUnit = TimeUnit.DAYS
            )
        )
    }

    companion object {

        private inline fun <reified T : ListenableWorker> oneTimeRequest(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<T>()
                .setNaiveDelay()
                .build()
        }

        private inline fun <reified T : ListenableWorker> periodicRequest(
            delay: Duration? = null,
            interval: Long,
            timeUnit: TimeUnit
        ): PeriodicWorkRequest {
            return PeriodicWorkRequestBuilder<T>(
                repeatInterval = interval,
                repeatIntervalTimeUnit = timeUnit
            )
                .apply {
                    if (delay == null) {
                        setNaiveDelay()
                    } else {
                        setInitialDelay(delay)
                    }
                }
                .build()
        }

        private fun <T : WorkRequest.Builder<*, *>> T.setNaiveDelay(): T {
            // Delaying to allow user interact with possible permissions grant dialog before
            // the worker starts executing to avoid any permission related issues.
            setInitialDelay(
                Duration.of(
                    1,
                    ChronoUnit.MINUTES
                )
            )

            return this
        }
    }
}