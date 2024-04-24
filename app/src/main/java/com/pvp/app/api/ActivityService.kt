package com.pvp.app.api

import com.pvp.app.model.ActivityEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.Date


interface ActivityService : DocumentsCollection {

    override val identifier: String
        get() = "activities"

    /**
     * Finds and returns an activity by the date and email from the database.
     * If not found returns null.
     */
    suspend fun get(
        date: LocalDate,
        email: String
    ): Flow<ActivityEntry?>

    /**
     * Creates or updates the specified activity in the database
     */
    suspend fun merge(activity: ActivityEntry)
}