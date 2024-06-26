package com.pvp.app.di

import android.content.Context
import androidx.work.WorkManager
import coil.Coil
import coil.ImageLoader
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.app
import com.pvp.app.api.ActivityService
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.Configuration
import com.pvp.app.api.DecorationService
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.FeedbackService
import com.pvp.app.api.FriendService
import com.pvp.app.api.GoalService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.ImageService
import com.pvp.app.api.MealService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.PointService
import com.pvp.app.api.RewardService
import com.pvp.app.api.SettingService
import com.pvp.app.api.StreakService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.api.WorkService
import com.pvp.app.service.ActivityServiceImpl
import com.pvp.app.service.AuthenticationServiceImpl
import com.pvp.app.service.ConfigurationImpl
import com.pvp.app.service.DecorationServiceImpl
import com.pvp.app.service.ExerciseServiceImpl
import com.pvp.app.service.ExperienceServiceImpl
import com.pvp.app.service.FeedbackServiceImpl
import com.pvp.app.service.FriendServiceImpl
import com.pvp.app.service.GoalServiceImpl
import com.pvp.app.service.HealthConnectServiceImpl
import com.pvp.app.service.ImageServiceImpl
import com.pvp.app.service.MealServiceImpl
import com.pvp.app.service.NotificationServiceImpl
import com.pvp.app.service.PointServiceImpl
import com.pvp.app.service.RewardServiceImpl
import com.pvp.app.service.SettingServiceImpl
import com.pvp.app.service.StreakServiceImpl
import com.pvp.app.service.TaskServiceImpl
import com.pvp.app.service.UserServiceImpl
import com.pvp.app.service.WorkServiceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServiceBindingsModule {

    @Binds
    @Singleton
    fun bindActivityService(service: ActivityServiceImpl): ActivityService

    @Binds
    @Singleton
    fun bindAuthenticationService(service: AuthenticationServiceImpl): AuthenticationService

    @Binds
    @Singleton
    fun bindConfiguration(service: ConfigurationImpl): Configuration

    @Binds
    @Singleton
    fun bindDecorationService(service: DecorationServiceImpl): DecorationService

    @Binds
    @Singleton
    fun bindExerciseService(service: ExerciseServiceImpl): ExerciseService

    @Binds
    @Singleton
    fun bindExperienceService(service: ExperienceServiceImpl): ExperienceService

    @Binds
    @Singleton
    fun bindFeedbackService(service: FeedbackServiceImpl): FeedbackService

    @Binds
    @Singleton
    fun bindFriendService(service: FriendServiceImpl): FriendService

    @Binds
    @Singleton
    fun bindGoalService(service: GoalServiceImpl): GoalService

    @Binds
    @Singleton
    fun bindHealthConnectService(service: HealthConnectServiceImpl): HealthConnectService

    @Binds
    @Singleton
    fun bindImageService(service: ImageServiceImpl): ImageService

    @Binds
    @Singleton
    fun bindMealService(service: MealServiceImpl): MealService

    @Binds
    @Singleton
    fun bindNotificationService(service: NotificationServiceImpl): NotificationService

    @Binds
    @Singleton
    fun bindPointService(service: PointServiceImpl): PointService

    @Binds
    @Singleton
    fun bindRewardService(service: RewardServiceImpl): RewardService

    @Binds
    @Singleton
    fun bindSettingService(service: SettingServiceImpl): SettingService

    @Binds
    @Singleton
    fun bindStreakService(service: StreakServiceImpl): StreakService

    @Binds
    @Singleton
    fun bindTaskService(service: TaskServiceImpl): TaskService

    @Binds
    @Singleton
    fun bindUserService(service: UserServiceImpl): UserService

    @Binds
    @Singleton
    fun bindWorkService(service: WorkServiceImpl): WorkService
}

@Module
@InstallIn(SingletonComponent::class)
object ServiceProvidersModule {

    @Provides
    @Singleton
    fun provideFirebaseApp(): FirebaseApp {
        return Firebase.app
    }

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext
        context: Context
    ): ImageLoader {
        return Coil.imageLoader(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext
        context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}