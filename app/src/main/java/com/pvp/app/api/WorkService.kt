package com.pvp.app.api

interface WorkService {

    /**
     * Creates notification channels under the application for user to configure
     * notification settings before any of the workers are actually initiated.
     */
    fun createNotificationChannels()

    /**
     * Initiates the activity worker.
     * The activity worker is responsible for updating the user's activity points
     */
    fun initiateActivityWorker()

    fun initiateAutocompleteWorker()

    fun initiateDailyTaskWorker()

    fun initiateDrinkReminderWorker()

    fun initiateGoalMotivationWorker()

    fun initiateTaskNotificationWorker()

    fun initiateTaskPointsDeductionWorker()

    fun initiateWeeklyActivitiesWorker()
}