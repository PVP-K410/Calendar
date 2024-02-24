package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.TaskService
import com.pvp.app.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskServiceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : TaskService {

    override suspend fun get(userEmail: String): Flow<Collection<Task>> {
        return database
            .collection(identifier)
            .whereEqualTo(Task::userEmail.name, userEmail)
            .snapshots()
            .map { qs ->
                qs.documents.mapNotNull { it.toObject(Task::class.java) }
            }
    }

    override suspend fun merge(task: Task) {
        if (task.id == null) {
            database
                .collection(identifier)
                .add(task)
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
            .set(task)
            .await()
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