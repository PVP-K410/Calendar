package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.PointService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.toJsonElement
import com.pvp.app.common.toPrimitivesMap
import com.pvp.app.model.MealTask
import com.pvp.app.model.Points
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class TaskServiceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val pointService: PointService,
    private val userService: UserService
) : TaskService {

    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Task::class, Task.serializer()) {
                subclass(MealTask::class, MealTask.serializer())
                subclass(SportTask::class, SportTask.serializer())
            }
        }
    }

    override suspend fun claim(
        task: Task
    ) {
        if (task.points.claimedAt != null) {
            error("Task points are already claimed")
        }

        task.points = task.points.copy(
            claimedAt = LocalDateTime.now()
        )

        userService
            .get(task.userEmail)
            .firstOrNull()
            ?.let { user ->
                userService.merge(
                    user.copy(
                        points = user.points + task.points.value
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
            value = pointService.calculate(task)
        )

        val reference = database
            .collection(identifier)
            .add(encodeByType(task))
            .await()

        reference
            .update(Task::id.name, reference.id)
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
            value = pointService.calculate(task)
        )

        val reference = database
            .collection(identifier)
            .add(encodeByType(task))
            .await()

        reference
            .update(Task::id.name, reference.id)
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
            value = pointService.calculate(task)
        )

        val reference = database
            .collection(identifier)
            .add(encodeByType(task))
            .await()

        reference
            .update(Task::id.name, reference.id)
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
    ) {
        TODO("Not yet implemented")
    }

    private fun decodeByType(
        task: Map<String, Any>
    ): Task {
        val element = task.toJsonElement()

        if (element !is JsonObject) {
            error("Task data is not a JSON object")
        }

        if (element.containsKey("recipe")) {
            return json.decodeFromJsonElement<MealTask>(element)
        }

        if (element.containsKey("activity")) {
            return json.decodeFromJsonElement<SportTask>(element)
        }

        return json.decodeFromJsonElement<Task>(element)
    }

    private fun encodeByType(
        task: Task
    ): Map<String, Any?> {
        return when (task) {
            is MealTask -> json
                .encodeToJsonElement<MealTask>(task)
                .toPrimitivesMap()

            is SportTask -> json
                .encodeToJsonElement<SportTask>(task)
                .toPrimitivesMap()

            else -> json
                .encodeToJsonElement<Task>(task)
                .toPrimitivesMap()
        }
    }

    override suspend fun get(
        userEmail: String
    ): Flow<List<Task>> {
        return database
            .collection(identifier)
            .whereEqualTo(Task::userEmail.name, userEmail)
            .snapshots()
            .map { qs ->
                qs.documents
                    .filter { it.exists() }
                    .mapNotNull { d -> d.data?.let { decodeByType(it) } }
            }
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