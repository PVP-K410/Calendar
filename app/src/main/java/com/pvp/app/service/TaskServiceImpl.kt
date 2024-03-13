package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.TaskService
import com.pvp.app.common.toJsonElement
import com.pvp.app.common.toPrimitivesMap
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import javax.inject.Inject

class TaskServiceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : TaskService {

    private val json = Json {
        serializersModule = SerializersModule {
            polymorphic(Task::class, Task.serializer()) {
                subclass(MealTask::class, MealTask.serializer())
                subclass(SportTask::class, SportTask.serializer())
            }
        }
    }

    override suspend fun get(userEmail: String): Flow<List<Task>> {
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

    private fun decodeByType(task: Map<String, Any>): Task {
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

    override suspend fun merge(task: Task) {
        if (task.id == null) {
            database
                .collection(identifier)
                .add(encodeByType(task))
                .continueWith { refTask ->
                    refTask.onSuccessTask { ref ->
                        ref.update(Task::id.name, ref.id)
                    }
                }
                .await()

            return
        }

        database
            .collection(identifier)
            .document(task.id)
            .set(encodeByType(task))
            .await()
    }

    private fun encodeByType(task: Task): Map<String, Any?> {
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

    override suspend fun remove(task: Task) {
        if (task.id == null) {
            throw IllegalArgumentException("Task id is required to remove it")
        }

        database
            .collection(identifier)
            .document(task.id)
            .delete()
            .await()
    }
}