package com.pvp.app.ui.screen.authentication

import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.pvp.app.ui.router.RouteUnauthenticated
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun UnauthenticatedScreen(
    controller: NavHostController,
    scope: CoroutineScope,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = Unit) {
        if (viewModel.isAuthenticated()) {
            controller.navigate(RouteUnauthenticated.Authenticated.route)
        }
    }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    viewModel.signIn(result.data ?: return@launch)
                }
            }
        }
    )

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.isSuccessful) {
        if (state.isSuccessful) {
            Toast
                .makeText(
                    context,
                    "Signed in successfully",
                    Toast.LENGTH_LONG
                )
                .show()

            controller.navigate(RouteUnauthenticated.Authenticated.route)
        } else if (state.messageError != null) {
            Toast
                .makeText(
                    context,
                    state.messageError,
                    Toast.LENGTH_LONG
                )
                .show()
        }
    }

    SignInScreen(
        onSignIn = {
            scope.launch {
                launcher.launch(
                    viewModel.buildSignInRequest()
                )
            }
        },
        state = state
    )
}