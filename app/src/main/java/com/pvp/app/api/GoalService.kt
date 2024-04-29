package com.pvp.app.api

import com.pvp.app.model.Goal
import com.pvp.app.model.SportActivity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GoalService: DocumentsCollection {

    override val identifier: String
        get() = "goals"

    /**
     * Creates a new goal in the database.
     */
    suspend fun create(
        activity: SportActivity,
        endDate: LocalDate,
        email: String,
        goal: Double,
        monthly: Boolean,
        startDate: LocalDate,
        steps: Boolean
    )

    suspend fun claim(
        goal: Goal
    )

    /**
     * Gets all goals for the specified email.
     */
    suspend fun get(email: String): Flow<List<Goal>>

    /**
     * Gets all goals for the specified email and start date.
     */
    suspend fun get(
        email: String,
        startDate: LocalDate
    ): Flow<List<Goal>>

    /**
     * Updates the goal in the database.
     */
    suspend fun update(goal: Goal)
}