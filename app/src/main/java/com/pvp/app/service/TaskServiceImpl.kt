package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.Configuration
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.PointService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.JSON
import com.pvp.app.common.resetTime
import com.pvp.app.common.toJsonElement
import com.pvp.app.common.toPrimitivesMap
import com.pvp.app.model.MealTask
import com.pvp.app.model.Points
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class TaskServiceImpl @Inject constructor(
    private val configuration: Configuration,
    private val database: FirebaseFirestore,
    private val experienceService: ExperienceService,
    private val pointService: PointService,
    private val userService: UserService
) : TaskService {

    override suspend fun claim(
        task: Task
    ) {
        if (task.points.claimedAt != null) {
            error("Task points are already claimed")
        }

        val now = LocalDateTime.now()

        if (
            task.points.isExpired && (
                    task.scheduledAt.year != now.year ||
                            task.scheduledAt.dayOfYear < (now.dayOfYear -
                            configuration.limitPointsReclaimDays) ||
                            task.scheduledAt.dayOfYear > now.dayOfYear
                    )
        ) {
            return
        }

        val tasksReclaimed = get(task.userEmail)
            .map {
                val date = task.scheduledAt.toLocalDate()

                it
                    .filter { t ->
                        t.scheduledAt
                            .toLocalDate()
                            .isEqual(date)
                    }
                    .filter { t -> t.points.claimedAt != null }
                    .filter { t -> t.points.isExpired }
            }
            .first().size

        if (tasksReclaimed >= configuration.limitPointsDeduction) {
            return
        }

        task.points = task.points.copy(
            claimedAt = now
        )

        userService
            .get(task.userEmail)
            .firstOrNull()
            ?.let { user ->
                val points = task.points.value + (if (task.points.isExpired) 1 else 0)
                val experience = user.experience + points
                val level = experienceService.levelOf(experience)

                userService.merge(
                    user.copy(
                        experience = experience,
                        level = level,
                        points = user.points + points
                    )
                )
            }
            ?: error("User not found while claiming task points")

        update(
            task,
            false
        )
    }

    override suspend fun create(
        description: String?,
        duration: Duration?,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ): Task {
        val task = Task(
            description = description,
            duration = duration,
            id = null,
            isCompleted = false,
            points = Points(),
            scheduledAt = scheduledAt,
            title = title,
            userEmail = userEmail
        )

        task.points = task.points.copy(
            isExpired = task.scheduledAt
                .toLocalDate()
                .isBefore(LocalDate.now()),
            value = pointService.calculate(task)
        )

        val reference = database
            .collection(identifier)
            .add(encodeByType(task))
            .await()

        reference
            .update(
                Task::id.name,
                reference.id
            )
            .await()

        val snapshot = reference
            .get()
            .await()

        return snapshot.data
            ?.let { decodeByType(it) }
            ?: error("General task creation failed")
    }

    override suspend fun create(
        activity: SportActivity,
        description: String?,
        distance: Double?,
        duration: Duration?,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ): SportTask {
        val task = SportTask(
            activity = activity,
            description = description,
            distance = distance,
            duration = duration,
            id = null,
            isCompleted = false,
            isDaily = false,
            points = Points(),
            scheduledAt = scheduledAt,
            title = title,
            userEmail = userEmail
        )

        task.points = task.points.copy(
            isExpired = task.scheduledAt
                .toLocalDate()
                .isBefore(LocalDate.now()),
            value = pointService.calculate(
                task = task,
                increasePointYield = isWeekly(activity)
            )
        )

        val reference = database
            .collection(identifier)
            .add(encodeByType(task))
            .await()

        reference
            .update(
                Task::id.name,
                reference.id
            )
            .await()

        val snapshot = reference
            .get()
            .await()

        return snapshot.data
            ?.let { decodeByType(it) } as? SportTask
            ?: error("Sport task creation failed")
    }

    override suspend fun create(
        description: String?,
        duration: Duration?,
        recipe: String,
        scheduledAt: LocalDateTime,
        title: String,
        userEmail: String
    ): MealTask {
        val task = MealTask(
            description = description,
            duration = duration,
            id = null,
            isCompleted = false,
            points = Points(),
            recipe = recipe,
            scheduledAt = scheduledAt,
            title = title,
            userEmail = userEmail
        )

        task.points = task.points.copy(
            isExpired = task.scheduledAt
                .toLocalDate()
                .isBefore(LocalDate.now()),
            value = pointService.calculate(task)
        )

        val reference = database
            .collection(identifier)
            .add(encodeByType(task))
            .await()

        reference
            .update(
                Task::id.name,
                reference.id
            )
            .await()

        val snapshot = reference
            .get()
            .await()

        return snapshot.data
            ?.let { decodeByType(it) } as? MealTask
            ?: error("Meal task creation failed")
    }

    override suspend fun generateDaily(
        count: Int,
        userEmail: String
    ): List<SportTask> {
        // TODO: Remove these lines with empty line below upon PR create
        println("generateDaily called")

        return SportActivity.entries
            .minus(SportActivity.Wheelchair)
            .shuffled()
            .take(count)
            .mapIndexed { index, activity ->
                val task = SportTask(
                    activity = activity,
                    description = "One of your daily tasks for today",
                    distance = null,
                    duration = null,
                    isCompleted = false,
                    isDaily = true,
                    points = Points(),
                    scheduledAt = LocalDateTime
                        .now()
                        .resetTime(),
                    title = "Task #${index + 1}: ${activity.title}",
                    userEmail = userEmail
                )

                if (activity.supportsDistanceMetrics) {
                    task.distance = (1000..5000 step 50)
                        .toList()
                        .random()
                        .toDouble()
                } else {
                    task.duration = Duration.ofMinutes(
                        (10..90 step 5)
                            .toList()
                            .random()
                            .toLong()
                    )
                }

                task.points = task.points.copy(
                    value = pointService.calculate(task)
                )

                task
            }
    }

    private fun decodeByType(
        task: Map<String, Any>
    ): Task {
        val element = task.toJsonElement()

        if (element !is JsonObject) {
            error("Task data is not a JSON object")
        }

        if (element.containsKey(MealTask::recipe.name)) {
            return JSON.decodeFromJsonElement<MealTask>(element)
        }

        if (element.containsKey(SportTask::activity.name)) {
            return JSON.decodeFromJsonElement<SportTask>(element)
        }

        return JSON.decodeFromJsonElement<Task>(element)
    }

    private fun encodeByType(
        task: Task
    ): Map<String, Any?> {
        return when (task) {
            is MealTask -> JSON
                .encodeToJsonElement<MealTask>(task)
                .toPrimitivesMap()

            is SportTask -> JSON
                .encodeToJsonElement<SportTask>(task)
                .toPrimitivesMap()

            else -> JSON
                .encodeToJsonElement<Task>(task)
                .toPrimitivesMap()
        }
    }

    override suspend fun get(
        userEmail: String
    ): Flow<List<Task>> {
        return database
            .collection(identifier)
            .whereEqualTo(
                Task::userEmail.name,
                userEmail
            )
            .snapshots()
            .map { qs ->
                qs.documents
                    .filter { it.exists() }
                    .mapNotNull { d -> d.data?.let { decodeByType(it) } }
            }
    }

    private suspend fun isWeekly(activity: SportActivity): Boolean {
        return userService.user
            .firstOrNull()?.weeklyActivities
            ?.contains(activity)
            ?: false
    }

    override suspend fun remove(
        task: Task
    ) {
        if (task.id == null) {
            error("Task id is required to remove it")
        }

        database
            .collection(identifier)
            .document(task.id)
            .delete()
            .await()
    }

    override suspend fun update(
        task: Task,
        updatePoints: Boolean
    ): Task {
        if (task.id == null) {
            error("Task id is required to update it.")
        }

        if (updatePoints && task.points.claimedAt == null) {
            task.points = task.points.copy(
                value = pointService.calculate(task)
            )
        }

        database
            .collection(identifier)
            .document(task.id)
            .set(encodeByType(task))
            .await()

        return when (task) {
            is MealTask -> MealTask.copy(task)
            is SportTask -> SportTask.copy(task)
            else -> Task.copy(task)
        }
    }
}