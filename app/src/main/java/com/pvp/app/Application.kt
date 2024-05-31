package com.pvp.app

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import com.pvp.app.api.WorkService
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class Application : Application(), Configuration.Provider, ImageLoaderFactory {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration
            .Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

    @Inject
    lateinit var workService: WorkService

    override fun onCreate() {
        super.onCreate()

        setupWorkersAndChannels()
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

    private fun setupWorkersAndChannels() {
        with(workService) {
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
    }
}