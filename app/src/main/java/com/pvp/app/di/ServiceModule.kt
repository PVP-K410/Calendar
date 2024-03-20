package com.pvp.app.di

import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.Configuration
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.NotificationService
import com.pvp.app.api.SettingService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.service.AuthenticationServiceImpl
import com.pvp.app.service.ConfigurationImpl
import com.pvp.app.service.ExerciseServiceImpl
import com.pvp.app.service.HealthConnectServiceImpl
import com.pvp.app.service.NotificationServiceImpl
import com.pvp.app.service.SettingServiceImpl
import com.pvp.app.service.TaskServiceImpl
import com.pvp.app.service.UserServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ServiceModule {

    @Binds
    @Singleton
    fun bindActivityService(service: HealthConnectServiceImpl): HealthConnectService

    @Binds
    @Singleton
    fun bindAuthenticationService(service: AuthenticationServiceImpl): AuthenticationService

    @Binds
    @Singleton
    fun bindConfiguration(service: ConfigurationImpl): Configuration

    @Binds
    @Singleton
    fun bindExerciseService(service: ExerciseServiceImpl): ExerciseService

    @Binds
    @Singleton
    fun bindNotificationService(service: NotificationServiceImpl): NotificationService

    @Binds
    @Singleton
    fun bindSettingService(service: SettingServiceImpl): SettingService

    @Binds
    @Singleton
    fun bindTaskService(service: TaskServiceImpl): TaskService

    @Binds
    @Singleton
    fun bindUserService(service: UserServiceImpl): UserService
}