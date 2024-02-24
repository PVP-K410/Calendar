package com.pvp.app.api

import com.pvp.app.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskService : DocumentsCollection {

    override val identifier: String
        get() = "tasks"

    /**
     * Gets all tasks for the given user by its email.
     */
    suspend fun get(userEmail: String): Flow<Collection<Task>>

    /**
     * Creates or updates the task in the database.
     */
    suspend fun merge(task: Task)

    /**
     * Removes a task from the database.
     */
    suspend fun remove(task: Task)
}