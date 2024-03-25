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
import com.pvp.app.common.toEpochSecondTimeZoned
import com.pvp.app.model.NotificationChannel
import com.pvp.app.worker.DrinkReminderWorker
import com.pvp.app.worker.TaskPointsDeductionWorkerSetup
import com.pvp.app.worker.WeeklyActivityWorkerSetup
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
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()

        createDrinkReminderWorker()

        createNotificationChannels()

        createTaskPointsDeductionWorker()

        createWeeklyActivitiesWorker()
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
        workManager.enqueue(
            OneTimeWorkRequestBuilder<WeeklyActivityWorkerSetup>()
                .setInitialDelay(
                    Duration.of(
                        1,
                        ChronoUnit.MINUTES
                    )
                )
                .build()
        )
    }
}
