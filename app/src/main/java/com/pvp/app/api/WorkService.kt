package com.pvp.app.api

interface WorkService {

    /**
     * Creates notification channels under the application for user to configure
     * notification settings before any of the workers are actually initiated.
     */
    fun createNotificationChannels()

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
     * Initiates the meal plan worker.
     * The meal plan worker is responsible for updating user's weekly meal plan.
     */
    fun initiateMealPlanWorker()

    /**
     * Initiates the task notification worker.
     * The task notification worker is responsible for sending notifications to user
     * for their incoming tasks.
     */
    fun initiateTaskNotificationWorker()

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
}