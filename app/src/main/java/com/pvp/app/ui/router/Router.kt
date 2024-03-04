package com.pvp.app.ui.router

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.CoroutineScope

@Composable
fun Router(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    scope: CoroutineScope
) {
    run {
        // Initialize beforehand or values are not set fast enough.
        Route.routes
    }

    NavHost(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(500)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(500)
            )
        },
        modifier = modifier,
        navController = controller,
        startDestination = Route.SignIn.route
    ) {
        Route.routes.forEach { r ->
            composable(route = r.route) {
                r.screen(
                    controller,
                    scope
                )
            }
        }
    }
}
