@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.ui.screen.survey

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.Configuration
import com.pvp.app.api.UserService
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.model.Survey
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val configuration: Configuration,
    @ApplicationContext private val context: Context,
    private val userService: UserService
) : ViewModel() {

    val state = userService.user
        .filterNotNull()
        .mapLatest { user ->
            val surveys = Survey.entries - user.surveys.toSet()

            SurveyState(
                current = surveys.firstOrNull(),
                surveys = surveys,
                user = user
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SurveyState()
        )

    fun <T> fromConfiguration(function: (Configuration) -> T): T {
        return function(configuration)
    }

    fun updateBodyMassIndex(
        height: Int,
        mass: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = state.first()

            state.current ?: return@launch

            userService.merge(
                state.user.copy(
                    height = height,
                    mass = mass,
                    surveys = state.user.surveys + state.current
                )
            )
        }
    }

    fun updateUserFilters(
        filters: List<String>,
        isActivities: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = state.first()

            state.current ?: return@launch

            if (isActivities) {
                val pairs = SportActivity.entries.associateBy { context.getString(it.titleId) }

                userService.merge(
                    state.user.copy(
                        activities = filters.mapNotNull { pairs[it] },
                        ingredients = state.user.ingredients,
                        surveys = state.user.surveys + state.current
                    )
                )
            } else {
                val pairs = Ingredient.entries.associateBy { context.getString(it.titleId) }

                userService.merge(
                    state.user.copy(
                        activities = state.user.activities,
                        ingredients = filters.mapNotNull { pairs[it] },
                        surveys = state.user.surveys + state.current
                    )
                )
            }
        }
    }
}

data class SurveyState(
    val current: Survey? = null,
    val surveys: List<Survey> = emptyList(),
    val user: User = User()
)