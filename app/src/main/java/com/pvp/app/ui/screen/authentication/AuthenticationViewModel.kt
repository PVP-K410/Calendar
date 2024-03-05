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

    suspend fun buildSignInRequest(): IntentSenderRequest? {
        val intentSender = authenticationService.beginSignIn() ?: return null

        return IntentSenderRequest
            .Builder(intentSender)
            .build()
    }

    suspend fun signIn(
        intent: Intent,
        onSignIn: suspend (AuthenticationResult) -> Unit = {}
    ) {
        val result = authenticationService.signIn(intent)

        onSignIn(result)
    }

    suspend fun signUp(
        intent: Intent,
        onSignUp: suspend (AuthenticationResult) -> Unit = {}
    ) {
        val result = authenticationService.signIn(intent) { result ->
            if (result.isSuccess && result.data?.email != null) {
                val user = userService
                    .get(result.data.email)
                    .firstOrNull()

                if (user == null) {
                    userService.merge(
                        User(
                            email = result.data.email,
                            height = 0,
                            mass = 0,
                            points = 0,
                            username = result.data.username
                        )
                    )
                }
            }
        }

        onSignUp(result)
    }
}