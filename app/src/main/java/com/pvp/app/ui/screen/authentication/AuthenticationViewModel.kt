package com.pvp.app.ui.screen.authentication

import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.AuthenticationState
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) : ViewModel() {

    private val _state = MutableStateFlow(AuthenticationState())
    private val _user = MutableStateFlow<User?>(null)
    val state = _state.asStateFlow()
    val user = _user.asStateFlow()

    init {
        if (isAuthenticated()) {
            viewModelScope.launch {
                userService
                    .get(authenticationService.user!!.email!!)
                    .collect { u ->
                        _user.value = u
                    }
            }
        }
    }

    suspend fun buildSignInRequest(): IntentSenderRequest? {
        val intentSender = authenticationService.beginSignIn() ?: return null

        return IntentSenderRequest
            .Builder(intentSender)
            .build()
    }

    fun isAuthenticated(): Boolean {
        return authenticationService.user != null
    }

    suspend fun signIn(intent: Intent) {
        val result = authenticationService.signIn(intent)

        if (result.data?.email != null) {
            userService
                .get(result.data.email)
                .collect { u ->
                    if (u == null) {
                        userService.merge(
                            User(
                                email = result.data.email,
                                height = 0,
                                mass = 0,
                                points = 0,
                                username = result.data.username
                            )
                        )

                        _user.value = userService
                            .get(result.data.email)
                            .first()
                    } else {
                        _user.value = u
                    }
                }
        }

        _state.update {
            it.copy(
                isSuccessful = result.data?.email != null,
                messageError = result.messageError,
            )
        }
    }
}