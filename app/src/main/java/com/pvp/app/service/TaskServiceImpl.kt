package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.Configuration
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.PointService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.JsonUtil.JSON
import com.pvp.app.common.JsonUtil.toJsonElement
import com.pvp.app.common.JsonUtil.toPrimitivesMap
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
import java.time.LocalTime
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

class TaskServiceImpl @Inject constructor(
    private val configuration: Configuration,
    private val database: FirebaseFirestore,
    private val exerciseService: ExerciseService,
    private val experienceService: ExperienceService,
    private val pointService: PointService,
    private val userService: UserService
) : TaskService {

    override suspend fun claim(task: Task) {
        if (task.points.claimedAt != null) {
            error("Task points are already claimed")
        }

        val now = LocalDateTime.now()

        if (
            task.points.isExpired && (
                    task.date.year != now.year ||
                            task.date.dayOfYear < (now.dayOfYear -
                            configuration.limitPointsReclaimDays) ||
                            task.date.dayOfYear > now.dayOfYear
                    )
        ) {
            return
        }

        val tasksReclaimed = get(task.userEmail)
            .map {
                val date = task.date

                it
                    .filter { t -> t.date.isEqual(date) }
                    .filter { t -> t.points.claimedAt != null }
                    .filter { t -> t.points.isExpired }
            }
            .first().size

        if (tasksReclaimed >= configuration.limitPointsDeduction) {
            return
        }

        task.points = task.points.copy(claimedAt = now)

        userService
            .get(task.userEmail)
            .firstOrNull()
            ?.let { user ->
                val points = task.points.value + (if (task.points.isExpired) 1 else 0)
                val experience = user.experience + points

                userService.merge(
                    user.copy(
                        experience = experience,
                        level = experienceService.levelOf(experience),
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
        date: LocalDate,
        description: String?,
        duration: Duration?,
        time: LocalTime?,
        title: String,
        userEmail: String
    ): Task {
        val task = Task(
            date = date,
            description = description,
            duration = duration,
            id = null,
            isCompleted = false,
            points = Points(),
            time = time?.cleanEnd(),
            title = title,
            userEmail = userEmail
        )

        task.points = task.points.copy(
            isExpired = task.date.isBefore(LocalDate.now()),
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
        date: LocalDate,
        description: String?,
        distance: Double?,
        duration: Duration?,
        isDaily: Boolean,
        time: LocalTime?,
        title: String,
        userEmail: String
    ): SportTask {
        val task = SportTask(
            activity = activity,
            date = date,
            description = description,
            distance = distance,
            duration = duration,
            id = null,
            isCompleted = false,
            isDaily = isDaily,
            points = Points(),
            time = time?.cleanEnd(),
            title = title,
            userEmail = userEmail
        )

        task.points = task.points.copy(
            isExpired = task.date.isBefore(LocalDate.now()),
            value = pointService.calculate(
                task = task,
                increasePointYield = isWeekly(
                    activity,
                    userService
                )
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
        date: LocalDate,
        description: String?,
        duration: Duration?,
        recipe: String,
        time: LocalTime?,
        title: String,
        userEmail: String
    ): MealTask {
        val task = MealTask(
            date = date,
            description = description,
            duration = duration,
            id = null,
            isCompleted = false,
            points = Points(),
            recipe = recipe,
            time = time?.cleanEnd(),
            title = title,
            userEmail = userEmail
        )

        task.points = task.points.copy(
            isExpired = task.date.isBefore(LocalDate.now()),
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
        return SportActivity.entries
            .minus(SportActivity.Wheelchair)
            .minus(SportActivity.Other)
            .shuffled()
            .take(count)
            .mapIndexed { index, activity ->
                val task = create(
                    activity = activity,
                    date = LocalDate.now(),
                    description = "One of your daily tasks for today",
                    distance = if (activity.supportsDistanceMetrics) {
                        getDistance(
                            activity = activity,
                            exerciseService = exerciseService
                        )
                    } else {
                        null
                    },
                    duration = if (!activity.supportsDistanceMetrics) {
                        getDuration(
                            activity = activity,
                            exerciseService = exerciseService
                        )
                    } else {
                        null
                    },
                    isDaily = true,
                    title = "Task #${index + 1}: ${activity.title}",
                    userEmail = userEmail
                )

                task
            }
    }

    override suspend fun get(userEmail: String): Flow<List<Task>> {
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

    override suspend fun remove(task: Task) {
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

        task.time = task.time?.cleanEnd()

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

    companion object {

        private fun LocalTime.cleanEnd(): LocalTime {
            return withSecond(0)
                .withNano(0)
        }

        private fun decodeByType(task: Map<String, Any>): Task {
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

        private fun encodeByType(task: Task): Map<String, Any?> {
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

        /**
         * @param baseDistance Represents a value (in km) that a user that walks 1km everyday
         * (activity level 1) would have as a maximum value assigned for a walking task
         */
        private suspend fun getDistance(
            activity: SportActivity,
            baseDistance: Double = 0.75,
            exerciseService: ExerciseService
        ): Double {
            val unit = baseDistance * 1000 / (1 / SportActivity.Walking.pointsRatioDistance)

            val multiplier = String
                .format(
                    "%.2f",
                    exerciseService.calculateActivityLevel()
                )
                .toDouble()

            val upperBound = (unit * (1 / activity.pointsRatioDistance) * (multiplier) / 10)
                .roundToInt() * 10

            val lowerBound = if (multiplier < 2.0) {
                // For ensuring users don't get task to walk 50 meters
                upperBound / 2
            } else {
                (unit * (1 / activity.pointsRatioDistance) * (multiplier - 1) / 10).roundToInt() * 10
            }

            return (lowerBound..upperBound step 50)
                .toList()
                .random()
                .toDouble() / 1000
        }

        /**
         * @param baseDuration Represents a value that a user that walks 1km everyday (activity level 1)
         * would have as a maximum value assigned for playing basketball
         */
        private suspend fun getDuration(
            activity: SportActivity,
            baseDuration: Duration = Duration.ofMinutes(30),
            exerciseService: ExerciseService
        ): Duration {
            val unit = baseDuration.seconds / (1 / SportActivity.Basketball.pointsRatioDuration)

            val multiplier = String
                .format(
                    "%.2f",
                    exerciseService.calculateActivityLevel()
                )
                .toDouble()

            // Division and multiplication by 300 are there to ensure upper and lower bounds
            // are multiples of 5 minutes
            val upperBound = ((unit * (1 / activity.pointsRatioDuration) * multiplier) / 300)
                .roundToInt() * 300

            val lowerBound = if (multiplier < 2.0) {
                max(
                    upperBound / 2,
                    300
                ) / 300 * 300
            } else {
                ((unit * (1 / activity.pointsRatioDuration) * (multiplier - 1)) / 300).roundToInt() * 300
            }

            return Duration.ofSeconds(
                (lowerBound..upperBound step 300)
                    .toList()
                    .random()
                    .toLong()
            )
        }

        private suspend fun isWeekly(
            activity: SportActivity,
            userService: UserService
        ): Boolean {
            return userService.user
                .firstOrNull()?.weeklyActivities
                ?.contains(activity)
                ?: false
        }
    }
}