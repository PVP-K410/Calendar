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
    destinationStart: Route,
    modifier: Modifier = Modifier,
    routes: List<Route>,
    scope: CoroutineScope
) {
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
        navController = controller,
        startDestination = destinationStart.path
    ) {
        routes.forEach { r ->
            composable(route = r.path) {
                r.screen(
                    controller,
                    modifier,
                    scope
                )
            }
        }
    }
}
