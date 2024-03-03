package com.pvp.app.ui

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Task
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pvp.app.R
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.task.CreateGeneralTaskForm
import com.pvp.app.ui.screen.task.CreateMealTaskForm
import com.pvp.app.ui.screen.task.CreateSportTaskForm

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
        val routes = listOf(
            Calendar,
            CreateTaskMeal,
            CreateTaskGeneral,
            CreateTaskSport
        )
    }

    data object Calendar : Route(
        Icons.Outlined.CalendarMonth,
        "Calendar page button icon",
        R.string.route_calendar,
        "calendar",
        { CalendarScreen() }
    )

    data object CreateTaskMeal : Route(
        Icons.Outlined.Task,
        "Meal task creation page button icon",
        R.string.route_tasks_create_meal,
        "tasks/create/meal",
        screen = { CreateMealTaskForm() }
    )

    data object CreateTaskGeneral : Route(
        Icons.Outlined.Task,
        "General task creation page button icon",
        R.string.route_tasks_create_general,
        "tasks/create/general",
        screen = { CreateGeneralTaskForm() }
    )

    data object CreateTaskSport : Route(
        Icons.Outlined.Task,
        "Sport task creation page button icon",
        R.string.route_tasks_create_sport,
        "tasks/create/sport",
        screen = { CreateSportTaskForm() }
    )
}