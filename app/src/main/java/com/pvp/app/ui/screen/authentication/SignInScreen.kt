package com.pvp.app.ui.screen.authentication

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.pvp.app.ui.router.Route

@Composable
fun SignInScreen(
    controller: NavHostController
) {
    AuthenticationBox(
        isSignIn = true,
        onSignIn = {
            // TODO: Implement sign in
        },
        onSignUp = {
            controller.navigate(Route.SignUp.route)
        }
    )
}