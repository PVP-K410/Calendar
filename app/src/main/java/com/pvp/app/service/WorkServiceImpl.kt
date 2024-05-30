package com.pvp.app.service

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.pvp.app.api.SettingService
import com.pvp.app.api.WorkService
import com.pvp.app.common.DateUtil.toEpochSecondTimeZoned
import com.pvp.app.common.TimeUtil.asChronoUnit
import com.pvp.app.model.GlobalReceiverConstants
import com.pvp.app.model.NotificationChannel
import com.pvp.app.model.Setting
import com.pvp.app.worker.ActivityWorker
import com.pvp.app.worker.AutocompleteWorker
import com.pvp.app.worker.DailyTaskWorker
import com.pvp.app.worker.DrinkReminderWorker
import com.pvp.app.worker.GoalMotivationWorker
import com.pvp.app.worker.GoogleCalendarSynchronizationWorker
import com.pvp.app.worker.MealPlanWorker
import com.pvp.app.worker.TaskNotificationWorker
import com.pvp.app.worker.TaskPointsDeductionWorker
import com.pvp.app.worker.WeeklyActivityWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingService: SettingService,
    private val workManager: WorkManager
) : WorkService {

    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(AlarmManager::class.java)
    }

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context)
    }

    override fun <T : ListenableWorker> cancelWorkerExact(
        clazz: Class<T>,
        intent: Intent
    ) {
        val pendingIntent = resolvePendingIntent(
            clazz,
            context,
            intent
        )

        alarmManager.cancel(pendingIntent)

        pendingIntent.cancel()
    }

    override fun createNotificationChannels() {
        NotificationChannel.entries
            .filter { it != NotificationChannel.Unknown }
            .forEach { channel ->
                notificationManager.createNotificationChannel(
                    android.app.NotificationChannel(
                        channel.channelId,
                        channel.channelId,
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                )
            }
    }

    override fun <T : ListenableWorker> createWorkerIntent(clazz: Class<T>): Intent {
        return Intent(
            context,
            GlobalReceiver::class.java
        ).apply {
            putExtra(
                GlobalReceiverConstants.HANDLER_ID,
                WorkService.BROADCAST_RECEIVER_HANDLER_ID
            )

            putExtra(
                WorkService.BROADCAST_RECEIVER_WORKER_NAME,
                clazz.simpleName
            )
        }
    }

    override fun <T : ListenableWorker> createWorkerPeriodicIntent(
        clazz: Class<T>,
        interval: Long,
        timeUnit: TimeUnit
    ): Intent = createWorkerIntent(clazz).apply {
        putExtra(
            WorkService.BROADCAST_RECEIVER_REPEAT_INTERVAL,
            interval
        )

        putExtra(
            WorkService.BROADCAST_RECEIVER_REPEAT_TIME_UNIT,
            timeUnit.name
        )

        putExtra(
            WorkService.BROADCAST_RECEIVER_REPEATING,
            true
        )
    }

    override suspend fun handleWorkLogic() {
        createNotificationChannels()

        initiateAutocompleteWorker()

        initiateDailyTaskWorker()

        initiateDrinkReminderWorker()

        initiateGoalMotivationWorker()

        initiateGoogleCalendarSynchronizationWorker()

        initiateMealPlanWorker()

        initiateTaskNotificationWorker()

        initiateTaskPointsDeductionWorker()

        initiateWeeklyActivitiesWorker()
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
            workManager.oneTimeRequest<ActivityWorker>()

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
        workManager.periodicRequest<AutocompleteWorker>(
            interval = 15,
            timeUnit = TimeUnit.MINUTES
        )
    }

    override fun initiateDailyTaskWorker() {
        workManager.oneTimeRequest<DailyTaskWorker>()

        val now = LocalDateTime.now()

        val target = now
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay()

        scheduleWorkerExactPeriodic(
            clazz = DailyTaskWorker::class.java,
            delay = Duration.of(
                target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned(),
                ChronoUnit.SECONDS
            ),
            interval = 1,
            timeUnit = TimeUnit.DAYS
        )
    }

    override fun initiateDrinkReminderWorker() {
        workManager.oneTimeRequest<DrinkReminderWorker>()

        val now = LocalDateTime.now()

        val target = now
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay()

        scheduleWorkerExactPeriodic(
            clazz = DrinkReminderWorker::class.java,
            delay = Duration.of(
                target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned(),
                ChronoUnit.SECONDS
            ),
            interval = 1,
            timeUnit = TimeUnit.DAYS
        )
    }

    override fun initiateGoalMotivationWorker() {
        val now = LocalDateTime.now()

        if (now < now
                .withHour(19)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        ) {
            workManager.oneTimeRequest<DrinkReminderWorker>()
        }

        val target = now
            .toLocalDate()
            .plusDays(1)
            .atTime(
                10,
                0,
                0,
                0
            )

        scheduleWorkerExactPeriodic(
            clazz = GoalMotivationWorker::class.java,
            delay = Duration.of(
                target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned(),
                ChronoUnit.SECONDS
            ),
            interval = 1,
            timeUnit = TimeUnit.DAYS
        )
    }

    override suspend fun initiateGoogleCalendarSynchronizationWorker() {
        scheduleWorkerExactPeriodic(
            clazz = GoogleCalendarSynchronizationWorker::class.java,
            interval = 6,
            timeUnit = TimeUnit.HOURS
        )
    }

    override fun initiateMealPlanWorker() {
        workManager.oneTimeRequest<MealPlanWorker>()

        val dayOfWeek = LocalDate.now().dayOfWeek.value

        scheduleWorkerExactPeriodic(
            clazz = MealPlanWorker::class.java,
            delay = Duration.of(
                if (dayOfWeek == 1) {
                    0L
                } else {
                    8L - dayOfWeek
                },
                ChronoUnit.DAYS
            ),
            interval = 7,
            timeUnit = TimeUnit.DAYS
        )
    }

    override suspend fun initiateTaskNotificationWorker() {
        workManager.oneTimeRequest<TaskNotificationWorker>()

        val now = LocalDateTime.now()

        val target = now
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay()
            .minusMinutes(
                settingService
                    .get(Setting.Notifications.ReminderBeforeTaskMinutes)
                    .first()
                    .toLong()
            )

        scheduleWorkerExactPeriodic(
            clazz = TaskNotificationWorker::class.java,
            delay = Duration.of(
                target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned(),
                ChronoUnit.SECONDS
            ),
            interval = 1,
            timeUnit = TimeUnit.DAYS
        )
    }

    override fun initiateTaskPointsDeductionWorker() {
        workManager.oneTimeRequest<TaskPointsDeductionWorker>()

        val now = LocalDateTime.now()

        val target = now
            .toLocalDate()
            .plusDays(1)
            .atStartOfDay()

        scheduleWorkerExactPeriodic(
            clazz = TaskPointsDeductionWorker::class.java,
            delay = Duration.of(
                target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned(),
                ChronoUnit.SECONDS
            ),
            interval = 1,
            timeUnit = TimeUnit.DAYS
        )
    }

    override fun initiateWeeklyActivitiesWorker() {
        scheduleWorkerExactPeriodic(clazz = WeeklyActivityWorker::class.java,
            delay = Duration.of(LocalDate.now().dayOfWeek.value
                .let { dayOfWeek ->
                    if (dayOfWeek == 1) {
                        0
                    } else {
                        8 - dayOfWeek
                    }
                }
                .toLong(),
                ChronoUnit.DAYS),
            interval = 7,
            timeUnit = TimeUnit.DAYS)
    }

    private inline fun <reified T : ListenableWorker> initiate(request: WorkerRequest) {
        workManager.oneTimeRequest<T>(delay = Duration.ZERO)

        if (request.repeating) {
            scheduleWorkerExactPeriodic(
                clazz = T::class.java,
                delay = Duration.of(
                    request.repeatInterval,
                    request.repeatTimeUnit.asChronoUnit()
                ),
                interval = request.repeatInterval,
                timeUnit = request.repeatTimeUnit
            )
        }
    }

    override fun processWorkerRequest(intent: Intent) {
        var repeating = intent.getBooleanExtra(
            WorkService.BROADCAST_RECEIVER_REPEATING,
            false
        )

        val repeatInterval = intent.getLongExtra(
            WorkService.BROADCAST_RECEIVER_REPEAT_INTERVAL,
            0
        )

        val repeatTimeUnit = TimeUnit.valueOf(
            intent.getStringExtra(
                WorkService.BROADCAST_RECEIVER_REPEAT_TIME_UNIT
            ) ?: TimeUnit.MILLISECONDS.name
        )

        if (repeating && repeatInterval == 0L) {
            repeating = false
        }

        val request = WorkerRequest(
            repeating = repeating,
            repeatInterval = repeatInterval,
            repeatTimeUnit = repeatTimeUnit
        )

        when (val type = intent.getStringExtra(WorkService.BROADCAST_RECEIVER_WORKER_NAME)) {
            DailyTaskWorker::class.simpleName -> initiate<DailyTaskWorker>(request)
            DrinkReminderWorker::class.simpleName -> initiate<DrinkReminderWorker>(request)
            GoalMotivationWorker::class.simpleName -> initiate<GoalMotivationWorker>(request)
            GoogleCalendarSynchronizationWorker::class.simpleName -> initiate<GoogleCalendarSynchronizationWorker>(request)
            MealPlanWorker::class.simpleName -> initiate<MealPlanWorker>(request)
            TaskNotificationWorker::class.simpleName -> initiate<TaskNotificationWorker>(request)
            TaskPointsDeductionWorker::class.simpleName -> initiate<TaskPointsDeductionWorker>(request)
            WeeklyActivityWorker::class.simpleName -> initiate<WeeklyActivityWorker>(request)
            else -> throw IllegalArgumentException("Unknown worker type $type")
        }
    }

    override fun <T : ListenableWorker> scheduleWorkerExact(
        clazz: Class<T>,
        delay: Duration?
    ) {
        val intent = createWorkerIntent(clazz)

        cancelWorkerExact(
            clazz,
            intent
        )

        alarmManager.scheduleWorkInManager(
            clazz,
            context,
            delay,
            intent
        )
    }

    override fun <T : ListenableWorker> scheduleWorkerExactPeriodic(
        clazz: Class<T>,
        delay: Duration?,
        interval: Long,
        timeUnit: TimeUnit
    ) {
        val intent = createWorkerPeriodicIntent(
            clazz,
            interval,
            timeUnit
        )

        cancelWorkerExact(
            clazz,
            intent
        )

        alarmManager.scheduleWorkInManager(
            clazz,
            context,
            delay,
            intent
        )
    }

    companion object {

        /**
         * Enqueues a one-time work request for the given worker type.
         *
         * @param delay The delay before the worker starts executing. By default, a naive delay is set.
         */
        private inline fun <reified T : ListenableWorker> WorkManager.oneTimeRequest(
            delay: Duration? = null
        ) {
            enqueueUniqueWork(T::class.java.name,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<T>()
                    .apply {
                        if (delay == null) {
                            setNaiveDelay()
                        } else {
                            setInitialDelay(delay)
                        }
                    }
                    .build())
        }

        /**
         * Enqueues a periodic work request for the given worker type.
         *
         * @param delay The delay before the worker starts executing. By default, a naive delay is set.
         */
        private inline fun <reified T : ListenableWorker> WorkManager.periodicRequest(
            delay: Duration? = null,
            interval: Long,
            timeUnit: TimeUnit
        ) {
            enqueueUniquePeriodicWork(T::class.java.name + ".periodic",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                PeriodicWorkRequestBuilder<T>(
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
                    .build())
        }

        /**
         * Resolves the pending intent for the worker.
         *
         * @param clazz The class of the worker to resolve the pending intent for.
         * @param intent The intent that contains the worker request.
         *
         * @return The pending intent for the worker
         */
        private fun <T : ListenableWorker> resolvePendingIntent(
            clazz: Class<T>,
            context: Context,
            intent: Intent
        ): PendingIntent {
            return PendingIntent.getBroadcast(
                context,
                clazz.name.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        /**
         * Schedules a worker in the [AlarmManager] to be executed at the given delay from now.
         *
         * @param clazz The worker class to be scheduled.
         * @param delay The delay before the worker starts executing.
         * @param intent The intent to be broadcast when the worker is executed.
         */
        private fun <T : ListenableWorker> AlarmManager.scheduleWorkInManager(
            clazz: Class<T>,
            context: Context,
            delay: Duration?,
            intent: Intent
        ) {
            setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + (delay ?: Duration.ZERO).toMillis(),
                resolvePendingIntent(
                    clazz,
                    context,
                    intent
                )
            )
        }

        /**
         * Delaying to allow user interact with possible permissions grant dialog before
         * the worker starts executing to avoid any permission related issues.
         */
        private fun <T : WorkRequest.Builder<*, *>> T.setNaiveDelay(): T {
            setInitialDelay(
                Duration.of(
                    30,
                    ChronoUnit.SECONDS
                )
            )

            return this
        }
    }
}

private class WorkerRequest(
    val repeating: Boolean,
    val repeatInterval: Long,
    val repeatTimeUnit: TimeUnit
)