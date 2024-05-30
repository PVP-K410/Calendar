package com.pvp.app

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import com.pvp.app.common.DateUtil.toEpochSecondTimeZoned
import com.pvp.app.model.NotificationChannel
import com.pvp.app.worker.ActivityWorker
import com.pvp.app.worker.AutocompleteWorker
import com.pvp.app.worker.DailyTaskWorker
import com.pvp.app.worker.DailyTaskWorkerSetup
import com.pvp.app.worker.DrinkReminderWorker
import com.pvp.app.worker.GoalMotivationWorker
import com.pvp.app.worker.MealPlanWorker
import com.pvp.app.worker.TaskNotificationWorker
import com.pvp.app.worker.TaskPointsDeductionWorkerSetup
import com.pvp.app.worker.WeeklyActivityWorker
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider, ImageLoaderFactory {

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

        createAutocompleteWorker()

        createDailyTaskWorker()

        createDrinkReminderWorker()

        createGoalMotivationWorker()

        createMealPlanWorker()

        createNotificationChannels()

        createTaskNotificationWorker()

        createTaskPointsDeductionWorker()

        createWeeklyActivitiesWorker()
    }

    fun createActivityWorker() {
        val prefs = getSharedPreferences(
            "ActivityWorker",
            Context.MODE_PRIVATE
        )

        val lastExecutionTime = prefs.getLong(
            "LastExecutionTime",
            0
        )

        val currentTime = System.currentTimeMillis()

        if (currentTime - lastExecutionTime > TimeUnit.HOURS.toMillis(2)) {
            val request = OneTimeWorkRequestBuilder<ActivityWorker>()
                .setInitialDelay(
                    Duration.of(
                        30,
                        ChronoUnit.SECONDS
                    )
                )
                .build()

            workManager.enqueue(request)

            prefs
                .edit()
                .putLong(
                    "LastExecutionTime",
                    currentTime
                )
                .apply()
        }
    }

    private fun createAutocompleteWorker() {
        val request = PeriodicWorkRequestBuilder<AutocompleteWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            AutocompleteWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
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
        val request = PeriodicWorkRequestBuilder<DrinkReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            DrinkReminderWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    private fun createGoalMotivationWorker() {
        val request = PeriodicWorkRequestBuilder<GoalMotivationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            GoalMotivationWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    private fun createMealPlanWorker() {
        val request = OneTimeWorkRequestBuilder<MealPlanWorker>()
            .build()

        workManager.enqueue(request)

        val daysDelay = LocalDate
            .now().dayOfWeek.value
            .let { dayOfWeek ->
                if (dayOfWeek == 1) {
                    0
                } else {
                    8 - dayOfWeek
                }
            }

        val requestPeriodic = PeriodicWorkRequestBuilder<MealPlanWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(
                Duration.of(
                    daysDelay.toLong(),
                    ChronoUnit.DAYS
                )
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            MealPlanWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            requestPeriodic
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

    private fun createTaskNotificationWorker() {
        val request = PeriodicWorkRequestBuilder<TaskNotificationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .build()

        workManager.enqueueUniquePeriodicWork(
            TaskNotificationWorker.WORKER_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
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

    override fun newImageLoader(): ImageLoader = ImageLoader
        .Builder(this)
        .components { add(SvgDecoder.Factory()) }
        .diskCache {
            DiskCache
                .Builder()
                .directory(applicationContext.cacheDir.resolve("images"))
                .build()
        }
        .okHttpClient {
            OkHttpClient
                .Builder()
                .addInterceptor { chain ->
                    chain
                        .proceed(chain.request())
                        .newBuilder()
                        .removeHeader("cache-control")
                        .removeHeader("expires")
                        .addHeader(
                            "cache-control",
                            "public, max-age=259200"
                        )
                        .build()
                }
                .build()
        }
        .build()
}