package com.pvp.app.ui.router

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.ui.common.RouteIcon
import com.pvp.app.ui.common.RouteTitle
import com.pvp.app.ui.common.RouteUtil.hiltViewModel
import com.pvp.app.ui.router.Route.Node
import com.pvp.app.ui.router.Route.Root
import com.pvp.app.ui.screen.authentication.AuthenticationScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.decoration.DecorationScreen
import com.pvp.app.ui.screen.friends.FriendScreen
import com.pvp.app.ui.screen.friends.FriendsScreen
import com.pvp.app.ui.screen.friends.FriendsViewModel
import com.pvp.app.ui.screen.goals.GoalScreen
import com.pvp.app.ui.screen.settings.SettingsScreen
import com.pvp.app.ui.screen.statistics.StatisticsScreen
import com.pvp.app.ui.screen.steps.StepScreen
import com.pvp.app.ui.screen.survey.SurveyScreen

sealed class Route(val path: String) {

    data class Options(
        val icon: (@Composable () -> Unit)? = null,
        val title: (@Composable () -> Unit)? = null
    ) {

        companion object {

            val None = Options()
        }
    }

    sealed class Node(
        val compose: @Composable (
            backstack: NavBackStackEntry,
            controller: NavHostController,
            modifier: Modifier
        ) -> Unit,
        val options: Options = Options.None,
        path: String
    ) : Route(path)

    sealed class Root(
        val nodes: List<Node>,
        path: String,
        val start: Node
    ) : Route(path)
}

object Routes {

    /**
     * These routes are used when user state is set to **authenticated** and all required
     * surveys are filled.
     *
     * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenAuthenticated] layout.
     */
    val authenticated = listOf(
        Calendar,
        Decorations,
        FriendsRoot,
        Goals,
        None,
        Settings,
        Statistics,
        Steps
    )

    /**
     * These routes are used for simple navigation drawer implementation. Routes that are
     * provided here, will be displayed in the navigation drawer. Navigation drawer is only
     * available for authenticated users, hence all routes that are under this list should
     * also be under [authenticated] list.
     *
     * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenAuthenticated] layout.
     */
    val drawer = listOf(
        Calendar,
        Decorations,
        Friends,
        Goals,
        Settings,
        Statistics,
        Steps
    )

    /**
     * These routes are used when user state is set to **unauthenticated** or when user has
     * any surveys that are not filled yet, but **must** be.
     *
     * Routes are used within [com.pvp.app.ui.screen.layout.LayoutScreenUnauthenticated] layout.
     */
    val unauthenticated = listOf(
        Authentication,
        None,
        Survey
    )

    data object Authentication : Node(
        compose = { _, _, _ -> AuthenticationScreen() },
        path = "authentication"
    )

    data object Calendar : Node(
        compose = { _, _, m -> CalendarScreen(modifier = m) },
        options = Options(
            icon = {
                RouteIcon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    resourceId = R.string.route_calendar
                )
            },
            title = { RouteTitle(stringResource(R.string.route_calendar)) }
        ),
        path = "calendar"
    )

    data object Decorations : Node(
        compose = { _, _, m -> DecorationScreen(modifier = m) },
        options = Options(
            icon = {
                RouteIcon(
                    imageVector = Icons.Outlined.Storefront,
                    resourceId = R.string.route_decorations
                )
            },
            title = { RouteTitle(stringResource(R.string.route_decorations)) }
        ),
        path = "decorations"
    )

    data object Friend : Node(
        compose = { backstack, controller, modifier ->
            FriendScreen(
                controller = controller,
                model = backstack.hiltViewModel<FriendsViewModel>(controller),
                modifier = modifier
            )
        },
        options = Options(
            title = { RouteTitle(stringResource(R.string.route_friends)) }
        ),
        path = "friend"
    )

    data object Friends : Node(
        compose = { backstack, controller, modifier ->
            FriendsScreen(
                controller = controller,
                model = backstack.hiltViewModel<FriendsViewModel>(controller),
                modifier = modifier
            )
        },
        options = Options(
            icon = {
                RouteIcon(
                    imageVector = Icons.Outlined.People,
                    resourceId = R.string.route_friends
                )
            },
            title = { RouteTitle(stringResource(R.string.route_friends)) }
        ),
        path = "friends"
    )

    data object FriendsRoot : Root(
        nodes = listOf(
            Friend,
            Friends
        ),
        path = "friends-root",
        start = Friends
    )

    data object Goals : Node(
        compose = { _, _, m -> GoalScreen(modifier = m) },
        options = Options(
            icon = {
                RouteIcon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    resourceId = R.string.route_goals
                )
            },
            title = { RouteTitle(stringResource(R.string.route_goals)) }
        ),
        path = "goals"
    )

    data object None : Node(
        compose = { _, _, _ -> },
        path = "none"
    )

    data object Settings : Node(
        compose = { _, _, m -> SettingsScreen(modifier = m) },
        options = Options(
            icon = {
                RouteIcon(
                    imageVector = Icons.Outlined.Settings,
                    resourceId = R.string.route_settings
                )
            },
            title = { RouteTitle(stringResource(R.string.route_settings)) }
        ),
        path = "settings"
    )

    data object Statistics : Node(
        compose = { _, _, m -> StatisticsScreen(modifier = m) },
        options = Options(
            icon = {
                RouteIcon(
                    imageVector = Icons.Outlined.AutoGraph,
                    resourceId = R.string.route_statistics
                )
            },
            title = { RouteTitle(stringResource(R.string.route_statistics)) }
        ),
        path = "statistics"
    )

    @SuppressLint("NewApi")
    data object Steps : Node(
        compose = { _, _, m -> StepScreen(modifier = m) },
        options = Options(
            icon = {
                RouteIcon(
                    imageVector = Icons.AutoMirrored.Outlined.DirectionsWalk,
                    resourceId = R.string.route_steps
                )
            },
            title = { RouteTitle(stringResource(R.string.route_steps)) }
        ),
        path = "steps"
    )

    data object Survey : Node(
        compose = { _, _, _ -> SurveyScreen() },
        options = Options(
            title = { RouteTitle(stringResource(R.string.route_survey)) }
        ),
        path = "survey"
    )
}