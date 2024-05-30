@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.calendar

import android.content.Context
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import com.pvp.app.api.UserService
import com.pvp.app.model.Meal
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    @ApplicationContext context: Context,
    userService: UserService
) : ViewModel() {

    private val ingredientsToAvoid = userService.user
        .filterNotNull()
        .mapLatest { user -> user.ingredients.map { context.getString(it.titleId) } }

    fun getIngredientsToAvoid(meal: Meal): Flow<List<String>> {
        return ingredientsToAvoid.mapLatest { ingredients ->
            meal.recipe
                .first().steps
                .flatMap { step ->
                    step.ingredients
                        .filter { ingredient ->
                            ingredients.any {
                                ingredient.contains(
                                    it,
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