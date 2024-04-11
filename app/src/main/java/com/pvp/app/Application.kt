package com.pvp.app

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pvp.app.common.DateUtil.toEpochSecondTimeZoned
import com.pvp.app.model.NotificationChannel
import com.pvp.app.worker.DailyTaskWorker
import com.pvp.app.worker.DailyTaskWorkerSetup
import com.pvp.app.worker.DrinkReminderWorker
import com.pvp.app.worker.TaskAutocompleteWorker
import com.pvp.app.worker.TaskPointsDeductionWorkerSetup
import com.pvp.app.worker.WeeklyActivityWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var workManager: WorkManager

    override val workManagerConfiguration: Configuration
        get() = Configuration
            .Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()

        createDailyTaskWorker()

        createDrinkReminderWorker()

        createNotificationChannels()

        createTaskAutocompleteWorker()

        createTaskPointsDeductionWorker()

        createWeeklyActivitiesWorker()
    }

    private fun createDailyTaskWorker() {
        val requestFirstTime = OneTimeWorkRequestBuilder<DailyTaskWorker>()
            .build()

        workManager
            .beginWith(requestFirstTime)
            .enqueue()

        val now = LocalDateTime.now()

        val target = now
            .plusDays(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val delay = target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned()

        val requestOneTime = OneTimeWorkRequestBuilder<DailyTaskWorkerSetup>()
            .setInitialDelay(
                Duration.of(
                    delay,
                    ChronoUnit.SECONDS
                )
            )
            .build()

        workManager
            .beginWith(requestOneTime)
            .enqueue()
    }

    private fun createDrinkReminderWorker() {
        val drinkWorkerRequest = PeriodicWorkRequestBuilder<DrinkReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            DrinkReminderWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            drinkWorkerRequest
        )
    }

    private fun createNotificationChannels() {
        val manager = NotificationManagerCompat.from(this)

        NotificationChannel.entries.forEach { channelEnum ->
            if (channelEnum != NotificationChannel.Unknown) {
                val notificationChannel = android.app.NotificationChannel(
                    channelEnum.channelId,
                    channelEnum.channelId,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                manager.createNotificationChannel(notificationChannel)
            }
        }
    }

    private fun createTaskAutocompleteWorker(){
        val requestPeriodic = PeriodicWorkRequestBuilder<TaskAutocompleteWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            TaskAutocompleteWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            requestPeriodic
        )
        Log.e("AUTOCOMPLETE", "CREATED")
    }

    private fun createTaskPointsDeductionWorker() {
        val now = LocalDateTime.now()

        val target = now
            .plusDays(1)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val delay = target.toEpochSecondTimeZoned() - now.toEpochSecondTimeZoned()

        val requestOneTime = OneTimeWorkRequestBuilder<TaskPointsDeductionWorkerSetup>()
            .setInitialDelay(
                Duration.of(
                    delay,
                    ChronoUnit.SECONDS
                )
            )
            .build()

        workManager
            .beginWith(requestOneTime)
            .enqueue()
    }

    private fun createWeeklyActivitiesWorker() {
        val requestPeriodic = PeriodicWorkRequestBuilder<WeeklyActivityWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(
                Duration.of(
                    1,
                    ChronoUnit.MINUTES
                )
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WeeklyActivityWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            requestPeriodic
        )
    }
}
