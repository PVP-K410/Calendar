package com.pvp.app.ui.screen.authentication

import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvp.app.api.AuthenticationService
import com.pvp.app.model.AuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationService: AuthenticationService
) : ViewModel() {

    private val _state = MutableStateFlow(AuthenticationState())
    val state = _state.asStateFlow()

    fun authenticate(
        intent: Intent,
        isOneTap: Boolean,
        onAuthenticate: suspend (AuthenticationResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            authenticationService
                .signIn(
                    intent = intent,
                    isOneTap = isOneTap
                )
                .also { onAuthenticate(it) }

            _state.update { it.copy(isLoading = false) }
        }
    }

    fun beginSignInRequest(
        launcher: (Intent) -> Unit,
        launcherOneTap: (IntentSenderRequest) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                val requestOneTap = buildSignInRequestOneTap()

                if (requestOneTap == null) {
                    val request = buildSignInRequest()

                    launcher(request)

                    return@launch
                }

                launcherOneTap(requestOneTap)
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun buildSignInRequest(): Intent {
        return authenticationService.beginSignIn()
    }

    private suspend fun buildSignInRequestOneTap(): IntentSenderRequest? {
        val sender = authenticationService.beginSignInOneTap() ?: return null

        return IntentSenderRequest.Builder(sender)
            .build()
    }
}

data class AuthenticationState(
    val isLoading: Boolean = false
)