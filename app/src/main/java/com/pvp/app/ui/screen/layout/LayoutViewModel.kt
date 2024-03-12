package com.pvp.app.ui.screen.layout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
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

            val userFirebaseFlow = authenticationService.user

            val userAppFlow = userFirebaseFlow
                .filterNotNull()
                .flatMapLatest { userService.get(it.email!!) }

            combine(
                userAppFlow,
                userFirebaseFlow
            ) { userApp, userFirebase ->
                _state.update {
                    LayoutState(
                        isAuthenticated = userFirebase != null,
                        isSurveyFilled = userApp?.let { isSurveyFilled(it) },
                        user = MutableStateFlow(userApp)
                            .asStateFlow()
                    )
                }
            }
                .launchIn(viewModelScope)

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun isSurveyFilled(user: User): Boolean {
        return user.mass != 0 && user.height != 0
    }
}

data class LayoutState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val isSurveyFilled: Boolean? = null,
    val user: StateFlow<User?> = MutableStateFlow(null),
)