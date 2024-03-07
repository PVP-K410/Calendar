package com.pvp.app.ui.router

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.ui.screen.authentication.SignInScreen
import com.pvp.app.ui.screen.authentication.SignUpScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.profile.ProfileScreen
import com.pvp.app.ui.screen.steps.StepScreen
import com.pvp.app.ui.screen.task.CreateGeneralTaskForm
import com.pvp.app.ui.screen.task.CreateMealTaskForm
import com.pvp.app.ui.screen.task.CreateSportTaskForm
import kotlinx.coroutines.CoroutineScope

sealed class Route(
    val icon: ImageVector? = null,
    val iconDescription: String? = null,
    val resourceTitleId: Int,
    val route: String,
    val screen: @Composable (NavHostController, CoroutineScope) -> Unit
) {

    companion object {

        val routesAuthenticated = listOf(
            Calendar,
            CreateTaskGeneral,
            CreateTaskMeal,
            CreateTaskSport,
            Profile,
            Steps
        )

        val routesDrawer = listOf(
            Calendar,
            Profile,
            Steps
        )

        val routesUnauthenticated = listOf(
            SignIn,
            SignUp
        )
    }

    data object Calendar : Route(
        icon = Icons.Outlined.CalendarMonth,
        iconDescription = "Calendar page button icon",
        resourceTitleId = R.string.route_calendar,
        route = "calendar",
        screen = { _, _ -> CalendarScreen() }
    )

    data object CreateTaskMeal : Route(
        icon = Icons.Outlined.AddTask,
        iconDescription = "Meal task creation page button icon",
        resourceTitleId = R.string.route_tasks_create_meal,
        route = "tasks/create/meal",
        screen = { _, _ -> CreateMealTaskForm() }
    )

    data object CreateTaskGeneral : Route(
        icon = Icons.Outlined.AddTask,
        iconDescription = "General task creation page button icon",
        R.string.route_tasks_create_general,
        route = "tasks/create/general",
        screen = { _, _ -> CreateGeneralTaskForm() }
    )

    data object CreateTaskSport : Route(
        icon = Icons.Outlined.AddTask,
        iconDescription = "Sport task creation page button icon",
        resourceTitleId = R.string.route_tasks_create_sport,
        route = "tasks/create/sport",
        screen = { _, _ -> CreateSportTaskForm() }
    )

    data object Profile : Route(
        icon = Icons.Outlined.EditNote,
        iconDescription = "Profile page button icon",
        resourceTitleId = R.string.route_profile,
        route = "profile",
        screen = { _, _ -> ProfileScreen() }
    )

    data object Steps : Route(
        icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
        iconDescription = "Step counter page button icon",
        resourceTitleId = R.string.route_steps,
        route = "steps",
        screen = { _, _ -> StepScreen() }
    )

    data object SignIn : Route(
        route = "authentication/sign-in",
        resourceTitleId = R.string.route_authentication_sign_in,
        screen = { c, s -> SignInScreen(c, s) }
    )

    data object SignUp : Route(
        route = "authentication/sign-up",
        resourceTitleId = R.string.route_authentication_sign_up,
        screen = { c, s -> SignUpScreen(c, s) }
    )
}