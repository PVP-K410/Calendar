package com.pvp.app.ui.router

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.ui.screen.authentication.AuthenticationScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.decoration.DecorationScreen
import com.pvp.app.ui.screen.friends.FriendsScreen
import com.pvp.app.ui.screen.settings.SettingsScreen
import com.pvp.app.ui.screen.steps.StepScreen
import com.pvp.app.ui.screen.survey.SurveyScreen
import kotlinx.coroutines.CoroutineScope

sealed class Route(
    val icon: ImageVector? = null,
    val iconDescription: String? = null,
    val path: String,
    val resourceTitleId: Int,
    val screen: @Composable (NavHostController, Modifier, CoroutineScope) -> Unit
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
            Decorations,
            Friends,
            None,
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
            Decorations,
            Friends,
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
            Authentication,
            None,
            Survey
        )
    }

    data object Authentication : Route(
        path = "authentication",
        resourceTitleId = R.string.empty,
        screen = { _, _, _ -> AuthenticationScreen() }
    )

    data object Calendar : Route(
        icon = Icons.Outlined.CalendarMonth,
        iconDescription = "Calendar page button icon",
        path = "calendar",
        resourceTitleId = R.string.route_calendar,
        screen = { _, m, _ -> CalendarScreen(modifier = m) }
    )

    data object Decorations : Route(
        icon = Icons.Outlined.Storefront,
        iconDescription = "Decorations page button icon",
        path = "decorations",
        resourceTitleId = R.string.route_decorations,
        screen = { _, m, _ -> DecorationScreen(modifier = m) }
    )

    data object Decorations : Route(
        icon = Icons.Outlined.Storefront,
        iconDescription = "Decorations page button icon",
        path = "decorations",
        resourceTitleId = R.string.route_decorations,
        screen = { _, _ -> DecorationScreen() }
    )

    data object Friends : Route(
        icon = Icons.Outlined.People,
        iconDescription = "Friends page button icon",
        path = "friends",
        resourceTitleId = R.string.route_friends,
        screen = { _, m, _ -> FriendsScreen(modifier = m) }
    )

    data object None : Route(
        path = "none",
        resourceTitleId = R.string.empty,
        screen = { _, _, _ -> }
    )

    data object Settings : Route(
        icon = Icons.Outlined.Settings,
        iconDescription = "Settings page button icon",
        path = "settings",
        resourceTitleId = R.string.route_settings,
        screen = { _, m, _ -> SettingsScreen(modifier = m) }
    )

    @SuppressLint("NewApi")
    data object Steps : Route(
        icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
        iconDescription = "Step counter page button icon",
        path = "steps",
        resourceTitleId = R.string.route_steps,
        screen = { _, m, _ -> StepScreen(modifier = m) }
    )

    data object Survey : Route(
        path = "survey",
        resourceTitleId = R.string.route_survey,
        screen = { _, _, _ -> SurveyScreen() }
    )
}