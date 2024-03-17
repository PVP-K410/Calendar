package com.pvp.app.ui.screen.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
import com.pvp.app.model.Ingredient
import com.pvp.app.model.SportActivity
import com.pvp.app.model.Survey
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(SurveyState())
    val state = _state.asStateFlow()

    init {
        collectStateUpdates()
    }

    private fun collectStateUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            userService.user.collect {
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
        }
    }

    private fun continueWith(user: User) {
        _state.value.current?.let {
            viewModelScope.launch(Dispatchers.IO) {
                userService.merge(user.copy(surveys = user.surveys + it))
            }
        }
    }

    fun updateBodyMassIndex(
        height: Int,
        mass: Int
    ) {
        continueWith(
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
        continueWith(
            _state.value.user.let { user ->
                if (isActivities) {
                    user.copy(
                        activities = filters.mapNotNull { SportActivity.fromTitle(it) }
                    )
                } else {
                    user.copy(
                        ingredients = filters.mapNotNull { Ingredient.fromTitle(it) }
                    )
                }
            }
        )
    }
}

data class SurveyState(
    val current: Survey? = null,
    val surveys: List<Survey> = emptyList(),
    val user: User = User()
)