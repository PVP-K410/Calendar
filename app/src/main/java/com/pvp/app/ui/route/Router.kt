package com.pvp.app.ui.route

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pvp.app.R
import com.pvp.app.ui.view.component.calendar.Week

@Composable
fun Router(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {
    run {
        // Initialize singleton beforehand, else values are not set fast enough.
        Routes.routes
    }

    NavHost(
        modifier = modifier,
        navController = controller,
        startDestination = Routes.Calendar.route
    ) {
        Routes.routes.forEach { r ->
            composable(r.route) {
                r.screen(this)
            }
        }
    }
}

sealed class Routes(
    val icon: ImageVector,
    val route: String,
    val routeNameId: Int,
    val screen: @Composable AnimatedContentScope.() -> Unit
) {

    companion object {
        val routes = listOf(Calendar)
    }

    data object Calendar : Routes(
        Icons.Outlined.CalendarMonth,
        "calendar",
        R.string.route_calendar,
        {
            Week()
        }
    )
}