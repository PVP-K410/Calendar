package com.pvp.app.api

import com.pvp.app.model.Meal
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface MealService : DocumentsCollection {

    override val identifier: String
        get() = "meals"

    /**
     * Generates a week meal plan based on the meals in the database and user diet preferences.
     * The plan will contain 3 meals for each day of the week.
     *
     * @return a map of the week meal plan.
     */
    suspend fun generateWeekPlan(): Map<DayOfWeek, List<Meal>>

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