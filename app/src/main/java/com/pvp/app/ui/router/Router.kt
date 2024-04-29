package com.pvp.app.ui.router

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pvp.app.ui.common.LocalRouteOptionsApplier

@Composable
fun Router(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    routeModifier: Modifier = Modifier,
    routes: List<Route>,
    start: Route
) {
    NavHost(
        enterTransition = { fadeIn(tween(300)) },
        exitTransition = { fadeOut(tween(300)) },
        modifier = modifier,
        navController = controller,
        startDestination = when (start) {
            is Route.Root -> start.start.path
            is Route.Node -> start.path
        }
    ) {
        routes.forEach { route ->
            when (route) {
                is Route.Node -> {
                    composeRoute(
                        controller,
                        route,
                        routeModifier
                    )
                }

                is Route.Root -> {
                    navigation(
                        route = route.path,
                        startDestination = route.start.path
                    ) {
                        route.nodes.forEach { node ->
                            composeRoute(
                                controller,
                                node,
                                routeModifier
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun NavGraphBuilder.composeRoute(
    controller: NavHostController,
    route: Route.Node,
    routeModifier: Modifier
) {
    composable(route.path) { backstack ->
        var applierRequired by remember { mutableStateOf(false) }

        if (applierRequired) {
            LocalRouteOptionsApplier.current { route.options }

            applierRequired = false
        }

        route.compose(
            backstack,
            controller,
            routeModifier
        )

        LaunchedEffect(controller.currentDestination) {
            if (controller.currentDestination?.route == route.path) {
                applierRequired = true
            }
        }
    }
}