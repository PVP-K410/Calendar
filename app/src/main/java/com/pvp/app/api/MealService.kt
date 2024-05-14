package com.pvp.app.api

import com.pvp.app.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealService : DocumentsCollection {

    override val identifier: String
        get() = "meals"

    /**
     * @return a flow of all meals in the database.
     */
    suspend fun get(): Flow<List<Meal>>

    /**
     * @param query Query to search for.
     *
     * @return a flow of meals that match the given [query] in their name or ingredients.
     */
    suspend fun get(query: String): Flow<List<Meal>>

    /**
     * Merges the given [meal] into the database.
     * If the meal does not exist, it will be created.
     * If the meal already exists, it will be updated.
     *
     * @param meal Meal to merge.
     */
    suspend fun merge(meal: Meal)

    /**
     * Removes the given [meal] from the database.
     *
     * @param meal Meal to remove.
     */
    suspend fun remove(meal: Meal)
}