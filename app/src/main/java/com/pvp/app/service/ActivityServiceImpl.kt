package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.ActivityService
import com.pvp.app.common.JsonUtil.JSON
import com.pvp.app.common.JsonUtil.toJsonElement
import com.pvp.app.common.JsonUtil.toPrimitivesMap
import com.pvp.app.model.ActivityEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.encodeToJsonElement
import java.time.LocalDate
import javax.inject.Inject

class ActivityServiceImpl @Inject constructor(
    private val database: FirebaseFirestore,
) : ActivityService {

    override suspend fun get(
        date: LocalDate,
        email: String
    ): Flow<ActivityEntry> {
        return database
            .collection(identifier)
            .whereEqualTo(
                ActivityEntry::email.name,
                email
            )
            .whereEqualTo(
                ActivityEntry::date.name,
                date.toString()
            )
            .limit(1)
            .snapshots()
            .map { qs ->
                qs.documents.firstNotNullOfOrNull {
                    it.data?.let {
                        JSON.decodeFromJsonElement(
                            ActivityEntry.serializer(),
                            it.toJsonElement()
                        )
                    }
                } ?: ActivityEntry(date = date)
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
                JSON
                    .encodeToJsonElement<ActivityEntry>(activity)
                    .toPrimitivesMap()
            )
        }
            .await()
    }
}