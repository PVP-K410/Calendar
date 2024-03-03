package com.pvp.app.ui.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.model.RouteComposeAuthenticated
import com.pvp.app.model.RouteComposeUnauthenticated
import com.pvp.app.model.RouteShowcased
import com.pvp.app.ui.screen.authentication.AuthenticatedScreen
import com.pvp.app.ui.screen.authentication.UnauthenticatedScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.task.CreateGeneralTaskForm
import com.pvp.app.ui.screen.task.CreateMealTaskForm
import com.pvp.app.ui.screen.task.CreateSportTaskForm
import kotlinx.coroutines.CoroutineScope

sealed class RouteAuthenticated(
    override val icon: ImageVector,
    override val iconDescription: String,
    override val resourceTitleId: Int,
    override val route: String,
    override val screen: @Composable (NavHostController, NavHostController, CoroutineScope) -> Unit
) : RouteComposeAuthenticated, RouteShowcased {

    companion object {
        val routes = listOf(
            Calendar,
            CreateTaskGeneral,
            CreateTaskMeal,
            CreateTaskSport
        )
    }

    data object Calendar : RouteAuthenticated(
        Icons.Outlined.CalendarMonth,
        "Calendar page button icon",
        R.string.route_calendar,
        "calendar",
        { _, _, _ -> CalendarScreen() }
    )

    data object CreateTaskMeal : RouteAuthenticated(
        Icons.Outlined.AddTask,
        "Meal task creation page button icon",
        R.string.route_tasks_create_meal,
        "tasks/create/meal",
        screen = { _, _, _ -> CreateMealTaskForm() }
    )

    data object CreateTaskGeneral : RouteAuthenticated(
        Icons.Outlined.AddTask,
        "General task creation page button icon",
        R.string.route_tasks_create_general,
        "tasks/create/general",
        screen = { _, _, _ -> CreateGeneralTaskForm() }
    )

    data object CreateTaskSport : RouteAuthenticated(
        Icons.Outlined.AddTask,
        "Sport task creation page button icon",
        R.string.route_tasks_create_sport,
        "tasks/create/sport",
        screen = { _, _, _ -> CreateSportTaskForm() }
    )
}

sealed class RouteUnauthenticated(
    override val route: String,
    override val screen: @Composable (NavHostController, CoroutineScope) -> Unit
) : RouteComposeUnauthenticated {

    companion object {
        val routes = listOf(
            Authenticated,
            Unauthenticated,
        )
    }

    data object Authenticated : RouteUnauthenticated(
        "authenticated",
        { c, s -> AuthenticatedScreen(c, s) }
    )

    data object Unauthenticated : RouteUnauthenticated(
        "unauthenticated",
        { c, s -> UnauthenticatedScreen(c, s) }
    )
}