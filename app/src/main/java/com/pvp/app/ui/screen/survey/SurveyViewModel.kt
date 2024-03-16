package com.pvp.app.ui.screen.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.model.Survey
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(SurveyState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            userService
                .getCurrent()
                .mapLatest {
                    it?.let { user ->
                        _state.update {
                            val surveys = Survey.entries - user.surveys.toSet()

                            SurveyState(
                                current = surveys.firstOrNull(),
                                surveys = surveys,
                                user = user
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun updateBodyMassIndex(
        height: Int,
        mass: Int
    ) {
        updateState(
            _state.value.user.copy(
                height = height,
                mass = mass
            )
        )
    }

    fun updateUserFilters(
        filters: List<String>,
        isActivities: Boolean
    ) {
        _state.value.user.let { user ->
            val userNew = if (isActivities) {
                user.copy(
                    activities = filters.mapNotNull { SportActivity.fromTitle(it) }
                )
            } else {
                user.copy(
                    ingredients = filters.mapNotNull { Ingredient.fromTitle(it) }
                )
            }

            updateState(userNew)
        }
    }

    private fun updateState(user: User) {
        viewModelScope.launch {
            _state.update { state ->
                val survey = state.current ?: return@update state.copy(user = user)
                val userNew = user.copy(surveys = user.surveys + survey)

                userService.merge(userNew)

                val surveys = state.surveys.filter {
                    survey != it
                }

                state.copy(
                    current = surveys.firstOrNull(),
                    surveys = surveys,
                    user = userNew
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