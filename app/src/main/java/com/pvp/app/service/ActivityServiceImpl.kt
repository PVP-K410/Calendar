`package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.ActivityService
import com.pvp.app.model.ActivityEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject

class ActivityServiceImpl @Inject constructor(
    private val database: FirebaseFirestore,
) : ActivityService {
    override suspend fun get(
        date: Date,
        email: String
    ): Flow<ActivityEntry?> {
        return database
            .collection(identifier)
            .whereEqualTo(
                ActivityEntry::email.name,
                email
            )
            .whereEqualTo(
                ActivityEntry::date.name,
                date
            )
            .limit(1)
            .snapshots()
            .map { qs ->
                qs.documents.firstNotNullOfOrNull {
                    it.toObject(ActivityEntry::class.java)
                }
            }
    }

    override suspend fun merge(activity: ActivityEntry) {
        val reference = if (activity.id == null) {
            val ref = database
                .collection(identifier)
                .document()

            activity.id = ref.id

            ref
        } else {
            database
                .collection(identifier)
                .document(activity.id!!)
        }

        database.runTransaction { transaction ->
            transaction.set(
                reference,
                activity
            )
        }
            .await()
    }
}