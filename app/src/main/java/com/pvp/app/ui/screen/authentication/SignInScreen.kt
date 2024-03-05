package com.pvp.app.ui.screen.authentication

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.ui.common.showToast
import com.pvp.app.ui.router.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    controller: NavHostController,
    scope: CoroutineScope,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val textSuccess = stringResource(R.string.screen_sign_in_toast_success)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    viewModel.signIn(result.data ?: return@launch) {
                        context.showToast(
                            isSuccess = it.isSuccess,
                            messageError = it.messageError
                                ?: "Error has occurred while signing in. Please try again later",
                            messageSuccess = textSuccess
                        )
                    }
                }
            }
        }
    )

    AuthenticationBox(
        isSignIn = true,
        onSignIn = {
            scope.launch {
                val request = viewModel.buildSignInRequest()

                if (request == null) {
                    context.showToast(message = "Error has occurred. Make sure you have a google account!")

                    return@launch
                }

                launcher.launch(request)
            }
        },
        onSignUp = {
            controller.navigate(Route.SignUp.route)
        }
    )
}