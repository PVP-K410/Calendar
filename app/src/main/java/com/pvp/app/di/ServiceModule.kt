package com.pvp.app.di

import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
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
    fun bindTaskService(service: TaskServiceImpl): TaskService

    @Binds
    @Singleton
    fun bindUserService(service: UserServiceImpl): UserService
}