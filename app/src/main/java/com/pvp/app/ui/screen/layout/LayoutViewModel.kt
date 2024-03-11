package com.pvp.app.ui.screen.layout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
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

data class LayoutState(
    val isLoading: Boolean = false,
    val userApp: StateFlow<User?> = MutableStateFlow(null),
    val userFirebase: StateFlow<FirebaseUser?> = MutableStateFlow(null)
)

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
                        isLoading = false,
                        userApp = MutableStateFlow(userApp).asStateFlow(),
                        userFirebase = MutableStateFlow(userFirebase).asStateFlow()
                    )
                }
            }
                .launchIn(viewModelScope)
        }
    }

    fun areDetailsSurveyed(): Boolean {
        val user = _state.value.userApp.value!!

        return user.mass != 0 && user.height != 0
    }
}