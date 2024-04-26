package com.pvp.app.ui.router

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation

@Composable
fun Router(
    controller: NavHostController,
    modifier: Modifier = Modifier,
    onConsumeOptions: (Route.Options) -> Unit = {},
    routeModifier: Modifier = Modifier,
    routes: List<Route>,
    start: Route
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
                        onConsumeOptions,
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
                                onConsumeOptions,
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
    onConsumeOptions: (Route.Options) -> Unit,
    route: Route.Node,
    routeModifier: Modifier
) {
    composable(route.path) { backstack ->
        val options = route.resolveOptions(
            backstack,
            controller
        )

        LaunchedEffect(
            backstack.destination.route,
            options
        ) {
            if (backstack.destination.route == route.path) {
                onConsumeOptions(options)
            }
        }

        route.compose(
            backstack,
            controller,
            routeModifier
        ) { onConsumeOptions(options) }
    }
}