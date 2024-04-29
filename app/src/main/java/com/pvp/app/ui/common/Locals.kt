package com.pvp.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.pvp.app.ui.router.Route

private fun notProvided(name: String): Nothing = error("No $name provided")

/**
 * Composition local for background colors
 */
val LocalBackgroundColors = staticCompositionLocalOf<List<Color>> {
    notProvided("LocalBackgroundColors")
}

/**
 * Composition local for route options
 */
val LocalRouteOptions = staticCompositionLocalOf<Route.Options> {
    notProvided("LocalRouteOptions")
}

/**
 * Composition local for applying route options by using current route options
 */
val LocalRouteOptionsApplier = staticCompositionLocalOf<
        @Composable ((options: Route.Options) -> Route.Options) -> Unit> {
    notProvided("LocalRouteOptionsApplier")
}