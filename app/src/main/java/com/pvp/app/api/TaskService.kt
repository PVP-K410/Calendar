package com.pvp.app.api

import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalDateTime

interface TaskService : DocumentsCollection {

    override val identifier: String
        get() = "tasks"

    /**
     * Claims the task points for the user specified in [Task.userEmail] property. Upon success,
     * user's total points are updated.
     *
     * @param task task to claim points for
     */
    suspend fun claim(
        task: Task
    )

    /**
     * Creates a general task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param description description of the task
     * @param duration duration of the task
     * @param scheduledAt scheduled date and time of the task
     * @param title title of the task
     * @param userEmail user email to create the task for
     *
     * @return created general task
     */
    suspend fun create(
        description: String? = null,
        duration: Duration? = null,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ): Task

    /**
     * Creates a sport task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param activity sport activity of the task
     * @param description description of the task
     * @param distance distance of the task
     * @param duration duration of the task
     * @param scheduledAt scheduled date and time of the task
     * @param title title of the task
     * @param userEmail user email to create the task for
     *
     * @return created sport task
     */
    suspend fun create(
        activity: SportActivity,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ): SportTask

    /**
     * Creates a meal task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param description description of the task
     * @param duration duration of the task
     * @param recipe recipe of the meal
     * @param scheduledAt scheduled date and time of the task
     * @param title title of the task
     * @param userEmail user email to create the task for
     *
     * @return created meal task
     */
    suspend fun create(
        description: String? = null,
        duration: Duration? = null,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ): MealTask

    /**
     * Gets all tasks for the given user by its email.
     *
     * @param userEmail email of the user to get tasks for
     */
    suspend fun get(
        userEmail: String
    ): Flow<List<Task>>

    /**
     * Removes a task from the database.
     */
    suspend fun remove(task: Task)

    /**
     * Updates the task in the database.
     *
     * @param task task to update
     * @param updatePoints flag to update the points of the task by recalculating them
     */
    suspend fun update(
        task: Task,
        updatePoints: Boolean = false
    )
}