package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.GoalService
import com.pvp.app.api.PointService
import com.pvp.app.api.UserService
import com.pvp.app.common.JsonUtil.JSON
import com.pvp.app.common.JsonUtil.toJsonElement
import com.pvp.app.common.JsonUtil.toPrimitivesMap
import com.pvp.app.model.Goal
import com.pvp.app.model.Points
import com.pvp.app.model.SportActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class GoalServiceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val experienceService: ExperienceService,
    private val pointService: PointService,
    private val userService: UserService
) : GoalService {
    override suspend fun create(
        activity: SportActivity,
        endDate: LocalDate,
        email: String,
        goal: Double,
        monthly: Boolean,
        startDate: LocalDate,
        steps: Boolean
    ) {
        val reference = database
            .collection(identifier)
            .document()

        val goalEntry = Goal(
            activity = activity,
            completed = false,
            endDate = endDate,
            email = email,
            goal = goal,
            monthly = monthly,
            id = reference.id,
            points = Points(),
            progress = 0.0,
            startDate = startDate,
            steps = steps
        )

        goalEntry.points = goalEntry.points.copy(
            isExpired = goalEntry.endDate.isBefore(LocalDate.now()),
            value = pointService.calculate(goalEntry)
        )

        database
            .runTransaction {
                it.set(
                    reference,
                    JSON
                        .encodeToJsonElement<Goal>(goalEntry)
                        .toPrimitivesMap()
                )
            }
            .await()
    }

    override suspend fun claim(
        goal: Goal
    ) {
        if (goal.points.claimedAt != null) {
            error("Goal points are already claimed")
        }

        goal.points = goal.points.copy(claimedAt = LocalDateTime.now())
        goal.completed = true

        userService
            .get(goal.email)
            .firstOrNull()
            ?.let { user ->
                val points = goal.points.value
                val experience = user.experience + points

                userService.merge(
                    user.copy(
                        experience = experience,
                        level = experienceService.levelOf(experience),
                        points = user.points + points
                    )
                )
            } ?: error("User not found while claiming goal")

        update(goal)
    }

    override suspend fun get(email: String): Flow<List<Goal>> {
        return database
            .collection(identifier)
            .whereEqualTo(
                Goal::email.name,
                email
            )
            .snapshots()
            .map { qs ->
                qs.documents
                    .filter { it.exists() }
                    .mapNotNull {
                        JSON.decodeFromJsonElement<Goal>(
                            it.data.toJsonElement()
                        )
                    }
            }
    }

    override suspend fun get(
        email: String,
        startDate: LocalDate
    ): Flow<List<Goal>> {
        return database
            .collection(identifier)
            .whereEqualTo(
                Goal::email.name,
                email
            )
            .whereEqualTo(
                Goal::startDate.name,
                startDate.toString()
            )
            .snapshots()
            .map { qs ->
                qs.documents
                    .filter { it.exists() }
                    .mapNotNull {
                        JSON.decodeFromJsonElement<Goal>(
                            it.data.toJsonElement()
                        )
                    }
            }
    }

    override suspend fun update(goal: Goal) {
        val reference = database
            .collection(identifier)
            .document(goal.id)

        database
            .runTransaction {
                it.set(
                    reference,
                    JSON
                        .encodeToJsonElement<Goal>(goal)
                        .toPrimitivesMap()
                )
            }
            .await()
    }
}