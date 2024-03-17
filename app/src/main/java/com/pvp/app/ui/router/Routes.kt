package com.pvp.app.ui.router

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.ui.screen.authentication.SignInScreen
import com.pvp.app.ui.screen.authentication.SignUpScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.calendar.MonthlyCalendarScreen
import com.pvp.app.ui.screen.settings.SettingsScreen
import com.pvp.app.ui.screen.steps.StepScreen
import com.pvp.app.ui.screen.survey.SurveyScreen
import kotlinx.coroutines.CoroutineScope

sealed class Route(
    val icon: ImageVector? = null,
    val iconDescription: String? = null,
    val path: String,
    val resourceTitleId: Int,
    val screen: @Composable (NavHostController, CoroutineScope) -> Unit
) {

    companion object {

        /**
         * These routes are used when user state is set to **authenticated** and all required
         * surveys are filled.
         *
         * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenAuthenticated] layout.
         */
        val routesAuthenticated = listOf(
            Calendar,
            MonthlyCalendar,
            Settings,
            Steps
        )

        /**
         * These routes are used for simple navigation drawer implementation. Routes that are
         * provided here, will be displayed in the navigation drawer. Navigation drawer is only
         * available for authenticated users, hence all routes that are under this list should
         * also be under [routesAuthenticated] list.
         *
         * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenAuthenticated] layout.
         */
        val routesDrawer = listOf(
            Calendar,
            MonthlyCalendar,
            Settings,
            Steps
        )

        /**
         * These routes are used when user state is set to **unauthenticated** or when user has
         * any surveys that are not filled yet, but **must** be.
         *
         * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenUnauthenticated] layout.
         */
        val routesUnauthenticated = listOf(
            SignIn,
            SignUp,
            Survey
        )
    }

    data object Calendar : Route(
        icon = Icons.Outlined.CalendarMonth,
        iconDescription = "Calendar page button icon",
        path = "calendar",
        resourceTitleId = R.string.route_calendar,
        screen = { _, _ -> CalendarScreen() }
    )

    data object MonthlyCalendar : Route(
        icon = Icons.Outlined.CalendarMonth,
        iconDescription = "Calendar page button icon",
        resourceTitleId = R.string.route_calendar_monthly,
        path = "calendar/monthly",
        screen = { _, _ -> MonthlyCalendarScreen() }
    )

    data object Settings : Route(
        icon = Icons.Outlined.Settings,
        iconDescription = "Settings page button icon",
        path = "settings",
        resourceTitleId = R.string.route_settings,
        screen = { _, _ -> SettingsScreen() }
    )

    @SuppressLint("NewApi")
    data object Steps : Route(
        icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
        iconDescription = "Step counter page button icon",
        path = "steps",
        resourceTitleId = R.string.route_steps,
        screen = { _, _ -> StepScreen() }
    )

    data object SignIn : Route(
        path = "authentication/sign-in",
        resourceTitleId = R.string.route_authentication_sign_in,
        screen = { c, s -> SignInScreen(c, s) }
    )

    data object SignUp : Route(
        path = "authentication/sign-up",
        resourceTitleId = R.string.route_authentication_sign_up,
        screen = { c, s -> SignUpScreen(c, s) }
    )

    data object Survey : Route(
        path = "survey",
        resourceTitleId = R.string.route_survey,
        screen = { _, _ -> SurveyScreen() }
    )
}