package com.pvp.app.ui.screen.authentication

import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.AuthenticationResult
import com.pvp.app.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val userService: UserService
) : ViewModel() {

    suspend fun buildSignInRequest(): Intent {
        return authenticationService.beginSignIn()
    }

    suspend fun buildSignInRequestOneTap(): IntentSenderRequest? {
        val sender = authenticationService.beginSignInOneTap() ?: return null

        return IntentSenderRequest
            .Builder(sender)
            .build()
    }

    private suspend fun handleSignUpResult(
        result: AuthenticationResult
    ) {
        if (!result.isSuccess || result.properties?.email == null) {
            return
        }

        val user = userService
            .get(result.properties.email)
            .firstOrNull()

        if (user == null) {
            userService.merge(
                User(
                    email = result.properties.email,
                    height = 0,
                    mass = 0,
                    points = 0,
                    username = result.properties.username
                )
            )
        }
    }

    suspend fun signIn(
        intent: Intent,
        isOneTap: Boolean,
        onSignIn: suspend (AuthenticationResult) -> Unit = {}
    ) {
        onSignIn(authenticationService.signIn(intent, isOneTap))
    }

    suspend fun signUp(
        intent: Intent,
        isOneTap: Boolean,
        onSignUp: suspend (AuthenticationResult) -> Unit = {}
    ) {
        onSignUp(authenticationService.signIn(intent, isOneTap) { handleSignUpResult(it) })
    }
}