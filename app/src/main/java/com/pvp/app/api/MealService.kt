package com.pvp.app.api

import com.pvp.app.model.Meal
import kotlinx.coroutines.flow.Flow

interface MealService : DocumentsCollection {

    override val identifier: String
        get() = "meals"

    /**
     * @return a flow of all meals in the database.
     */
    fun get(): Flow<List<Meal>>

    /**
     * @param id ID of the meal to get.
     *
     * @return a flow of the meal with the given [id].
     */
    fun get(id: String): Flow<Meal>

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