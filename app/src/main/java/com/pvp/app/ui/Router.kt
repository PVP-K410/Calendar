package com.pvp.app.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pvp.app.R
import com.pvp.app.ui.screen.calendar.CalendarScreen

@Composable
fun Router(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {
    run {
        // Initialize singleton beforehand, else values are not set fast enough.
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
        startDestination = Route.Calendar.route
    ) {
        Route.routes.forEach { r ->
            composable(route = r.route) {
                r.screen(this)
            }
        }
    }
}

sealed class Route(
    val icon: ImageVector,
    val iconDescription: String,
    val resourceTitleId: Int,
    val route: String,
    val screen: @Composable AnimatedContentScope.() -> Unit
) {

    companion object {
        val routes = listOf(Calendar)
    }

    data object Calendar : Route(
        Icons.Outlined.CalendarMonth,
        "Calendar page button icon",
        R.string.route_calendar,
        "calendar",
        { CalendarScreen() }
    )
}