package com.pvp.app.ui.router

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pvp.app.R
import com.pvp.app.ui.screen.authentication.AuthenticationScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.decoration.DecorationScreen
import com.pvp.app.ui.screen.friends.FriendScreen
import com.pvp.app.ui.screen.friends.FriendsScreen
import com.pvp.app.ui.screen.friends.FriendsViewModel
import com.pvp.app.ui.screen.settings.SettingsScreen
import com.pvp.app.ui.screen.steps.StepScreen
import com.pvp.app.ui.screen.survey.SurveyScreen
import kotlinx.coroutines.CoroutineScope

sealed class Route(
    val builder: NavGraphBuilder.(
        controller: NavHostController,
        modifier: Modifier,
        scope: CoroutineScope
    ) -> Unit,
    val icon: ImageVector? = null,
    val iconDescription: String? = null,
    val path: String,
    val resourceTitleId: Int
) {

    data object Authentication : Route(
        builder = { _, _, _ ->
            composable("authentication") {
                AuthenticationScreen()
            }
        },
        path = "authentication",
        resourceTitleId = R.string.empty,
    )

    data object Calendar : Route(
        builder = { _, m, _ ->
            composable("calendar") {
                CalendarScreen(modifier = m)
            }
        },
        icon = Icons.Outlined.CalendarMonth,
        iconDescription = "Calendar page button icon",
        path = "calendar",
        resourceTitleId = R.string.route_calendar
    )

    data object Decorations : Route(
        builder = { _, m, _ ->
            composable("decorations") {
                DecorationScreen(modifier = m)
            }
        },
        icon = Icons.Outlined.Storefront,
        iconDescription = "Decorations page button icon",
        path = "decorations",
        resourceTitleId = R.string.route_decorations
    )

    data object Friends : Route(
        builder = { controller, modifier, _ ->
            navigation(
                route = "friendsGraph",
                startDestination = "friends"
            ) {
                composable("friends") {
                    FriendsScreen(
                        controller = controller,
                        model = it.hiltViewModel<FriendsViewModel>(controller),
                        modifier = modifier
                    )
                }

                composable("friend") {
                    FriendScreen(
                        model = it.hiltViewModel<FriendsViewModel>(controller),
                        modifier = modifier
                    )
                }
            }
        },
        icon = Icons.Outlined.People,
        iconDescription = "Friends page button icon",
        path = "friendsGraph",
        resourceTitleId = R.string.route_friends
    )

    data object None : Route(
        builder = { _, _, _ -> composable("none") { } },
        path = "none",
        resourceTitleId = R.string.empty
    )

    data object Settings : Route(
        builder = { _, m, _ ->
            composable("settings") {
                SettingsScreen(modifier = m)
            }
        },
        icon = Icons.Outlined.Settings,
        iconDescription = "Settings page button icon",
        path = "settings",
        resourceTitleId = R.string.route_settings
    )

    @SuppressLint("NewApi")
    data object Steps : Route(
        builder = { _, m, _ ->
            composable("steps") {
                StepScreen(modifier = m)
            }
        },
        icon = Icons.AutoMirrored.Outlined.DirectionsWalk,
        iconDescription = "Step counter page button icon",
        path = "steps",
        resourceTitleId = R.string.route_steps
    )

    data object Survey : Route(
        builder = { _, _, _ ->
            composable("survey") {
                SurveyScreen()
            }
        },
        path = "survey",
        resourceTitleId = R.string.route_survey
    )

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

        /**
         * If there is any routes in the backstack that had specified viewModel, it will be
         * returned. Otherwise, it will return the viewModel of the current route.
         */
        @Composable
        private inline fun <reified T : ViewModel> NavBackStackEntry.hiltViewModel(
            controller: NavController
        ): T {
            val navGraphRoute = destination.parent?.route ?: return hiltViewModel()

            val parentEntry = remember(this) {
                controller.getBackStackEntry(navGraphRoute)
            }

            return hiltViewModel(parentEntry)
        }
    }
}