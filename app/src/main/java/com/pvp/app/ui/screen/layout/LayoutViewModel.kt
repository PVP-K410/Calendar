package com.pvp.app.ui.screen.layout

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.Survey
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class LayoutViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(LayoutState())
    val state: StateFlow<LayoutState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val flowUserFirebase = authenticationService.user

            val flowUserApp = flowUserFirebase
                .filterNotNull()
                .flatMapLatest { userService.get(it.email!!) }

            combine(
                flowUserApp,
                flowUserFirebase
            ) { userApp, userFirebase ->
                _state.update {
                    LayoutState(
                        areSurveysFilled = userApp?.let { areSurveysFilled(it) },
                        isAuthenticated = userFirebase != null,
                        user = userApp,
                        userAvatar = userApp?.let {
                            userService.resolveAvatar(it.email)
                        }
                    )
                }
            }
                .launchIn(viewModelScope)

            _state.update { it.copy(isLoading = false) }
        }
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