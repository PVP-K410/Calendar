package com.pvp.app.api

import com.pvp.app.model.ActivityEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date


interface ActivityService : DocumentsCollection {

    override val identifier: String
        get() = "activities"

    /**
     * @return ActivityEntry by email and date. Null if not found.
     */
    suspend fun get(
        date: Date,
        email: String
    ): Flow<ActivityEntry?>

    /**
     * Creates or updates the specified activity in the database
     */
    suspend fun merge(activity: ActivityEntry)
}