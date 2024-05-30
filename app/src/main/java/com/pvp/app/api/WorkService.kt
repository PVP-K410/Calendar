package com.pvp.app.api

import android.content.Intent
import androidx.work.ListenableWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

interface WorkService {

    /**
     * Cancels worker of the specified class.
     *
     * @param clazz The class of the worker to cancel.
     * @param intent The intent that contains the worker request. If worker is periodic,
     * [createWorkerPeriodicIntent] should be used to create the intent or else
     * [createWorkerIntent] should be used.
     */
    fun <T : ListenableWorker> cancelWorkerExact(
        clazz: Class<T>,
        intent: Intent
    )

    /**
     * Creates notification channels under the application for user to configure
     * notification settings before any of the workers are actually initiated.
     */
    fun createNotificationChannels()

    /**
     * Creates a worker intent for the specified class.
     *
     * @param clazz The class of the worker to create the intent for.
     *
     * @return The intent for the worker.
     */
    fun <T : ListenableWorker> createWorkerIntent(clazz: Class<T>): Intent

    /**
     * Creates a worker intent for the specified class that is supposed to be executed
     * periodically.
     *
     * @param clazz The class of the worker to create the intent for.
     * @param interval The interval at which the work should be repeated.
     * @param timeUnit The time unit of the interval.
     *
     * @return The intent for the worker.
     */
    fun <T : ListenableWorker> createWorkerPeriodicIntent(
        clazz: Class<T>,
        interval: Long,
        timeUnit: TimeUnit
    ): Intent

    /**
     * Handles all required work that is supposed to be done on application's background.
     *
     * It may check whether any of the services must be re-initiated and if so, will re-initiate
     * them, save some required data and etc. to ensure application works as intended.
     *
     * It may also create certain settings and/or data that is required for the application to
     * work properly.
     */
    suspend fun handleWorkLogic()

    /**
     * Initiates the activity worker.
     * The activity worker is responsible for updating user's activity data.
     */
    fun initiateActivityWorker()

    /**
     * Initiates the autocomplete worker.
     * The autocomplete worker is responsible for updating user's goals and tasks based on
     * user's activity data.
     */
    fun initiateAutocompleteWorker()

    /**
     * Initiates the daily task worker.
     * The daily task worker is responsible for updating user's daily tasks and removing
     * old ones that are not completed.
     */
    fun initiateDailyTaskWorker()

    /**
     * Initiates the drink reminder worker.
     * The drink reminder worker is responsible for reminding user to drink water.
     */
    fun initiateDrinkReminderWorker()

    /**
     * Initiates the goal motivation worker.
     * The goal motivation worker is responsible for motivating user to achieve their goals.
     */
    fun initiateGoalMotivationWorker()

    /**
     * Initiates the google calendar synchronization worker.
     * The google calendar synchronization worker is responsible for synchronizing user's
     * google calendar with the application.
     */
    suspend fun initiateGoogleCalendarSynchronizationWorker()

    /**
     * Initiates the meal plan worker.
     * The meal plan worker is responsible for updating user's weekly meal plan.
     */
    fun initiateMealPlanWorker()

    /**
     * Initiates the task notification worker.
     * The task notification worker is responsible for sending notifications to user
     * for their incoming tasks.
     */
    suspend fun initiateTaskNotificationWorker()

    /**
     * Initiates the task points deduction worker.
     * The task points deduction worker is responsible for deducting points from user
     * for their incomplete tasks.
     */
    fun initiateTaskPointsDeductionWorker()

    /**
     * Initiates the weekly activities worker.
     * The weekly activities worker is responsible for updating user's weekly activities.
     */
    fun initiateWeeklyActivitiesWorker()

    /**
     * Processes the work request.
     *
     * @param intent Intent that contains worker request.
     */
    fun processWorkerRequest(intent: Intent)

    /**
     * Schedules a work to be executed at a specific time in the future, calculated from now
     * based on the delay.
     *
     * @param clazz The class of the worker to schedule.
     * @param delay The delay before the work is executed.
     */
    fun <T : ListenableWorker> scheduleWorkerExact(
        clazz: Class<T>,
        delay: Duration? = null
    )

    /**
     * Schedules a work to be executed at a specific time in the future, calculated from now
     * based on the delay and then periodically at the specified interval.
     *
     * @param clazz The class of the worker to schedule.
     * @param delay The delay before the work is executed.
     * @param interval The interval at which the work should be repeated.
     * @param timeUnit The time unit of the interval.
     */
    fun <T : ListenableWorker> scheduleWorkerExactPeriodic(
        clazz: Class<T>,
        delay: Duration? = null,
        interval: Long,
        timeUnit: TimeUnit
    )

    companion object {

        /**
         * The value used to identify the broadcast receiver handler.
         * Value should be [String].
         */
        const val BROADCAST_RECEIVER_HANDLER_ID = "work"

        /**
         * The key for the broadcast receiver to identify the interval for the worker to repeat.
         * Value should be [Long].
         */
        const val BROADCAST_RECEIVER_REPEAT_INTERVAL = "worker_repeat_interval"

        /**
         * The key for the broadcast receiver to identify the time unit for the worker to repeat.
         * Value should be [TimeUnit].
         */
        const val BROADCAST_RECEIVER_REPEAT_TIME_UNIT = "worker_repeat_time_unit"

        /**
         * The key for the broadcast receiver to identify whether the worker should be repeating.
         * Value should be [Boolean].
         */
        const val BROADCAST_RECEIVER_REPEATING = "worker_repeating"

        /**
         * The key for the broadcast receiver to identify the worker. Stored value equals
         * to the worker's [Class.getSimpleName].
         */
        const val BROADCAST_RECEIVER_WORKER_NAME = "worker"
    }
}