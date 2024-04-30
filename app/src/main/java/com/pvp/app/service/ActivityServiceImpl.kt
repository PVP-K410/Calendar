package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.ActivityService
import com.pvp.app.common.DateUtil.toTimestamp
import com.pvp.app.common.JsonUtil.JSON
import com.pvp.app.common.JsonUtil.toJsonElement
import com.pvp.app.common.JsonUtil.toPrimitivesMap
import com.pvp.app.model.ActivityEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.LocalDate
import javax.inject.Inject

class ActivityServiceImpl @Inject constructor(
    private val database: FirebaseFirestore,
) : ActivityService {

    override suspend fun get(
        date: LocalDate,
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
                date.toString()
            )
            .limit(1)
            .snapshots()
            .map { qs ->
                qs.documents
                    .firstOrNull()
                    ?.let {
                        JSON.decodeFromJsonElement<ActivityEntry>(
                            it.data.toJsonElement()
                        )
                    }
            }
    }

    override suspend fun get(
        date: Pair<LocalDate, LocalDate>,
        email: String
    ): Flow<List<ActivityEntry>> {
        return database
            .collection(identifier)
            .whereEqualTo(
                ActivityEntry::email.name,
                email
            )
            .whereGreaterThanOrEqualTo(
                ActivityEntry::date.name,
                date.first.toTimestamp()
            )
            .whereLessThanOrEqualTo(
                ActivityEntry::date.name,
                date.second.toTimestamp()
            )
            .snapshots()
            .map { qs ->
                qs.documents.map { ds ->
                    JSON.decodeFromJsonElement<ActivityEntry>(
                        ds.data.toJsonElement()
                    )
                }
            }
    }

    override suspend fun merge(activity: ActivityEntry) {
        val reference = database
            .collection(identifier)
            .document(
                activity.id
                    ?: database
                        .collection(identifier)
                        .document()
                        .also { activity.id = it.id }.id
            )

        database
            .runTransaction {
                it.set(
                    reference,
                    JSON
                        .encodeToJsonElement<ActivityEntry>(activity)
                        .toPrimitivesMap()
                )
            }
            .await()
    }
}