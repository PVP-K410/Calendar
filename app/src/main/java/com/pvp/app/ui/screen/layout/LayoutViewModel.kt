package com.pvp.app.ui.screen.layout

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Survey
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LayoutViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(LayoutState())
    val state: StateFlow<LayoutState> = _state.asStateFlow()

    init {
        _state.update { it.copy(isLoading = true) }

        collectStateUpdates()
    }

    private fun collectStateUpdates() {
        combine(
            userService.user,
            authenticationService.user
        ) { userApp, userFirebase ->
            _state.update {
                LayoutState(
                    areSurveysFilled = userApp?.let { areSurveysFilled(it) },
                    isAuthenticated = userFirebase != null,
                    isLoading = false,
                    user = userApp,
                    userAvatar = userApp?.let {
                        userService.resolveAvatar(it.email)
                    }
                )
            }
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private fun areSurveysFilled(user: User): Boolean {
        return user.surveys.containsAll(Survey.entries)
    }
}

data class LayoutState(
    val areSurveysFilled: Boolean? = null,
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val user: User? = null,
    val userAvatar: ImageBitmap? = null
)