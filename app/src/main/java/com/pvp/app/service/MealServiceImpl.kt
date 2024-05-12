@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.MealService
import com.pvp.app.model.Meal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class MealServiceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : MealService {

    override fun get(): Flow<List<Meal>> {
        return database
            .collection(identifier)
            .snapshots()
            .mapLatest { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.toObject(Meal::class.java)
                }
            }
    }

    override suspend fun merge(meal: Meal) {
        database.runTransaction {
            if (meal.id.isBlank()) {
                val document = database
                    .collection(identifier)
                    .document()

                val mealNew = meal.copy(id = document.id)

                it.set(
                    document,
                    mealNew
                )
            } else {
                it.set(
                    database
                        .collection(identifier)
                        .document(meal.id),
                    meal
                )
            }
        }
    }

    override suspend fun remove(meal: Meal) {
        if (meal.id.isBlank()) {
            error("Meal id is empty, cannot remove meal from the database")
        }

        database.runTransaction {
            it.delete(
                database
                    .collection(identifier)
                    .document(meal.id)
            )
        }
    }
}