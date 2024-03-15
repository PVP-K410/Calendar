package com.pvp.app.ui.screen.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.UserService
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
        viewModelScope.launch {
            _state.value.user.let {
                userService.merge(
                    it.copy(
                        height = height,
                        mass = mass,
                        surveys = it.surveys + state.value.current!!
                    )
                )
            }
        }
    }

    fun updateUserFilters(
        filters: List<String>
    ) {
        viewModelScope.launch {
            _state.value.user.let { user ->
                userService.merge(
                    user.copy(
                        activities = filters.mapNotNull { SportActivity.fromTitle(it) },
                        surveys = user.surveys + state.value.current!!
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