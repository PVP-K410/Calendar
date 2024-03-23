package com.pvp.app

import android.app.Application
import android.app.NotificationManager
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.pvp.app.model.NotificationChannel
import com.pvp.app.service.DrinkReminderWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
        createDrinkReminderWorker()
    }

    private fun createNotificationChannels() {
        val manager = NotificationManagerCompat.from(this)

        NotificationChannel.entries.forEach { channelEnum ->
            if (channelEnum != NotificationChannel.UNKNOWN) {
                val notificationChannel = android.app.NotificationChannel(
                    channelEnum.channelId,
                    channelEnum.channelId,
                    NotificationManager.IMPORTANCE_DEFAULT
                )

                manager.createNotificationChannel(notificationChannel)
            }
        }
    }

    private fun createDrinkReminderWorker() {
        val drinkWorkerRequest = PeriodicWorkRequestBuilder<DrinkReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                DrinkReminderWorker.WORKER_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                drinkWorkerRequest
            )
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}
