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
import com.pvp.app.ui.common.navigateTo
import com.pvp.app.ui.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    controller: NavHostController,
    scope: CoroutineScope,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    if (viewModel.isAuthenticated()) {
        controller.navigateTo(Route.Calendar.route)

        return
    }

    val context = LocalContext.current
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

            controller.navigateTo(Route.Calendar.route)
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

    AuthenticationBox(
        isSignIn = true,
        onSignIn = {
            scope.launch {
                launcher.launch(
                    viewModel.buildSignInRequest()
                )
            }
        },
        onSignUp = {
            controller.navigateTo(Route.SignUp.route)
        }
    )
}