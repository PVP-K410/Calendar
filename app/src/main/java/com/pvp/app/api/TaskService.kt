package com.pvp.app.api

import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

interface TaskService : DocumentsCollection {

    override val identifier: String
        get() = "tasks"

    /**
     * Claims the task points for the user specified in [Task.userEmail] property. Upon success,
     * user's total points are updated.
     *
     * @param task task to claim points for
     */
    suspend fun claim(task: Task)

    /**
     * Creates a general task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param date scheduled date
     * @param description description
     * @param duration duration
     * @param time scheduled time
     * @param title title
     * @param userEmail user email to create the task for
     *
     * @return created general task
     */
    suspend fun create(
        date: LocalDate,
        description: String? = null,
        duration: Duration? = null,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ): Task

    /**
     * Creates a sport task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param date scheduled date
     * @param activity sport activity
     * @param description description
     * @param distance distance
     * @param duration duration
     * @param isDaily flag to create a daily task
     * @param time scheduled time
     * @param title title
     * @param userEmail user email to create the task for
     *
     * @return created sport task
     */
    suspend fun create(
        activity: SportActivity,
        date: LocalDate,
        description: String? = null,
        distance: Double? = null,
        duration: Duration? = null,
        isDaily: Boolean = false,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ): SportTask

    /**
     * Creates a meal task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param date scheduled date
     * @param description description
     * @param duration duration
     * @param recipe recipe of the meal
     * @param time scheduled time
     * @param title title
     * @param userEmail user email to create the task for
     *
     * @return created meal task
     */
    suspend fun create(
        date: LocalDate,
        description: String? = null,
        duration: Duration? = null,
        recipe: String,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ): MealTask

    /**
     * Generates daily tasks for the given user by its email.
     *
     * @param count number of tasks to generate
     * @param userEmail email of the user to generate daily tasks for
     *
     * @return list of generated tasks
     */
    suspend fun generateDaily(
        count: Int = 3,
        userEmail: String
    ): List<SportTask>

    /**
     * Gets all tasks for the given user by its email.
     *
     * @param userEmail email of the user to get tasks for
     *
     * @return flow of tasks
     */
    suspend fun get(userEmail: String): Flow<List<Task>>

    /**
     * Removes a task from the database.
     *
     * @param task task to remove
     */
    suspend fun remove(task: Task)

    /**
     * Updates the task in the database.
     *
     * @param task task to update
     * @param updatePoints flag to update the points by recalculating them
     *
     * @return updated task
     */
    suspend fun update(
        task: Task,
        updatePoints: Boolean = false
    ): Task
}