@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.MealService
import com.pvp.app.api.UserService
import com.pvp.app.model.Diet
import com.pvp.app.model.Meal
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.max

class MealServiceImpl @Inject constructor(
    private val database: FirebaseFirestore,
    private val userService: UserService
) : MealService {

    private val breakfastAliases = listOf(
        "appetizer",
        "breakfast",
        "salad",
        "side dish",
        "snack",
        "starter"
    )

    override suspend fun generateWeekPlan(): Map<DayOfWeek, List<Meal>> {
        val user = userService.user.firstOrNull()

        user ?: error("User is not logged in")

        val meals = get()
            .first()
            .filter {
                it.recipe
                    .first().steps
                    .none { step ->
                        step.ingredients.any { ingredient ->
                            user.ingredients
                                .map { ingredientBlocked -> ingredientBlocked.name }
                                .any { ingredientBlocked ->
                                    ingredient.contains(
                                        ingredientBlocked,
                                        ignoreCase = true
                                    )
                                }
                        }
                    }
            }

        val dayNow = LocalDate.now().dayOfWeek

        val daysToMeals = DayOfWeek.entries
            .toList()
            .subList(
                DayOfWeek.entries.indexOf(DayOfWeek.entries.first { it == dayNow }),
                DayOfWeek.entries.size
            )
            .associateWith { _ -> mutableListOf<Meal>() }

        var mealsBreakfast = meals
            .filter {
                when (user.diet) {
                    Diet.Carbohydrates -> it.nutrition.caloricBreakdown.percentCarbs
                    Diet.Fat -> it.nutrition.caloricBreakdown.percentFat
                    Diet.Protein -> it.nutrition.caloricBreakdown.percentProtein
                    else -> 100.0
                } >= 10
            }
            .filter {
                it.dishTypes.any { type ->
                    breakfastAliases.any { alias ->
                        alias.contains(
                            type,
                            ignoreCase = true
                        )
                    }
                }
            }

        require(mealsBreakfast.isNotEmpty()) {
            "No breakfast meals found. Please add some meals to the database."
        }

        daysToMeals.forEach { (day, mealsOfDay) ->
            mealsBreakfast = mealsBreakfast.shuffled()

            val breakfast = mealsBreakfast.firstOrNull()

            if (breakfast != null) {
                mealsBreakfast = mealsBreakfast.subList(
                    1,
                    mealsBreakfast.size
                )

                mealsOfDay.add(breakfast)
            } else {
                val breakfastOld = daysToMeals[day.minus(1)]?.firstOrNull()

                if (breakfastOld != null) {
                    mealsOfDay.add(breakfastOld)
                }
            }
        }

        val mealsMain = meals.filter {
            when (user.diet) {
                Diet.Carbohydrates -> it.nutrition.caloricBreakdown.percentCarbs
                Diet.Fat -> it.nutrition.caloricBreakdown.percentFat
                Diet.Protein -> it.nutrition.caloricBreakdown.percentProtein
                else -> 100.0
            } >= 40
        }

        require(mealsMain.isNotEmpty()) {
            "No main meals found. Please add some meals to the database and make sure " +
                    "they have at least 40% of the required nutrients."
        }

        daysToMeals.forEach { (day, mealsOfDay) ->
            if (mealsOfDay.size > 2 || mealsMain.isEmpty()) {
                return@forEach
            }

            repeat(2) {
                val main = mealsMain
                    .shuffled()
                    .firstOrNull()

                if (main == null) {
                    return@forEach
                }

                val servings = max(
                    3,
                    main.servings / 2
                )

                for (i in 0 until servings + 1) {
                    val dayTarget = day.plus(i.toLong())

                    if (dayTarget < day) {
                        break
                    }

                    val dayMeals = daysToMeals[dayTarget]!!

                    if (dayMeals.size < 3) {
                        dayMeals.add(main)
                    }
                }
            }
        }

        return daysToMeals
    }

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

    override fun get(id: String): Flow<Meal> {
        return database
            .collection(identifier)
            .document(id)
            .snapshots()
            .mapLatest { snapshot -> snapshot.toObject(Meal::class.java) }
            .filterNotNull()
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