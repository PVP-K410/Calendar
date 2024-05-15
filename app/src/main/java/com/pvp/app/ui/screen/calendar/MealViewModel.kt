@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.calendar

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import com.pvp.app.api.UserService
import com.pvp.app.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    userService: UserService
) : ViewModel() {

    private val ingredientsToAvoid = userService.user
        .filterNotNull()
        .mapLatest { user -> user.ingredients.map { it.title } }

    fun getIngredientsToAvoid(meal: Meal): Flow<List<String>> {
        return ingredientsToAvoid.mapLatest { ingredients ->
            meal.recipe
                .first().steps
                .flatMap { step ->
                    step.ingredients
                        .filter { ingredient ->
                            ingredients.any {
                                it.equals(
                                    ingredient,
                                    ignoreCase = true
                                )
                            }
                        }
                }
                .map {
                    it
                        .capitalize(Locale.current)
                        .trim()
                }
                .distinct()
                .sorted()
        }
    }
}