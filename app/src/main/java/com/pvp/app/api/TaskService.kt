package com.pvp.app.api

import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.Meal
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
     * @param reminderTime minutes before the task to send a reminder
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
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ): GeneralTask

    /**
     * Creates a sport task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param date scheduled date
     * @param activity sport activity
     * @param description description
     * @param distance distance
     * @param duration duration
     * @param reminderTime minutes before the task to send a reminder
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
        reminderTime: Duration? = null,
        isDaily: Boolean = false,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ): SportTask

    /**
     * Creates a custom meal task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param date scheduled date
     * @param duration duration
     * @param reminderTime minutes before the task to send a reminder
     * @param recipe recipe of the meal
     * @param time scheduled time
     * @param title title
     * @param userEmail user email to create the task for
     *
     * @return created custom meal task
     */
    suspend fun create(
        date: LocalDate,
        duration: Duration? = null,
        reminderTime: Duration? = null,
        recipe: String,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ): CustomMealTask

    /**
     * Creates a meal task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param date scheduled date
     * @param duration duration
     * @param mealId meal ID
     * @param reminderTime minutes before the task to send a reminder
     * @param time scheduled time
     * @param title title
     * @param userEmail user email to create the task for
     *
     * @return created custom meal task
     */
    suspend fun create(
        date: LocalDate,
        duration: Duration? = null,
        mealId: String,
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String,
        userEmail: String
    ): MealTask

    /**
     * Creates a meal task in the database with the given parameters. Points are calculated
     * automatically upon creation.
     *
     * @param date scheduled date
     * @param duration duration
     * @param meal meal
     * @param reminderTime minutes before the task to send a reminder
     * @param time scheduled time
     * @param title title. If not provided, [Meal.name] should be used within implementation
     * @param userEmail user email to create the task for
     *
     * @return created meal task
     */
    suspend fun create(
        date: LocalDate,
        duration: Duration? = null,
        meal: Meal,
        reminderTime: Duration? = null,
        time: LocalTime? = null,
        title: String? = null,
        userEmail: String
    ): MealTask

    /**
     * Generates daily tasks for the given user by its email.
     *
     * @param count number of tasks to generate
     * @param hasDisability flag to generate tasks for a user with a disability
     * @param userEmail email of the user to generate daily tasks for
     *
     * @return list of generated tasks
     */
    suspend fun generateDaily(
        count: Int = 3,
        hasDisability: Boolean,
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
     * Removes all tasks from the database.
     *
     * @param userEmail email of the user to remove tasks for
     */
    suspend fun removeAll(userEmail: String)

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