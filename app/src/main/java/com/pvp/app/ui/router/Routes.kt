package com.pvp.app.ui.router

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.pvp.app.R
import com.pvp.app.ui.common.RouteUtil.RouteIcon
import com.pvp.app.ui.common.RouteUtil.RouteTitle
import com.pvp.app.ui.common.RouteUtil.hiltViewModel
import com.pvp.app.ui.router.Route.Node
import com.pvp.app.ui.router.Route.Root
import com.pvp.app.ui.screen.authentication.AuthenticationScreen
import com.pvp.app.ui.screen.calendar.CalendarScreen
import com.pvp.app.ui.screen.decoration.DecorationScreen
import com.pvp.app.ui.screen.friends.FriendScreen
import com.pvp.app.ui.screen.friends.FriendsScreen
import com.pvp.app.ui.screen.friends.FriendsViewModel
import com.pvp.app.ui.screen.settings.SettingsScreen
import com.pvp.app.ui.screen.steps.StepScreen
import com.pvp.app.ui.screen.survey.SurveyScreen

sealed class Route(val path: String) {

    open class Options(
        val icon: (@Composable () -> Unit)? = null,
        val title: (@Composable () -> Unit)? = null
    ) {

        data object None : Options()
    }

    sealed class Node(
        val compose: @Composable (
            backstack: NavBackStackEntry,
            controller: NavHostController,
            modifier: Modifier,
            resolveOptions: () -> Unit
        ) -> Unit,
        val options: Options = Options.None,
        path: String,
        val resolveOptions: @Composable (
            NavBackStackEntry,
            NavHostController
        ) -> Options = { _, _ -> options }
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
    val routesAuthenticated = listOf(
        Calendar,
        Decorations,
        FriendsRoot,
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

    data object Authentication : Node(
        compose = { _, _, _, _ -> AuthenticationScreen() },
        path = "authentication"
    )

    data object Calendar : Node(
        compose = { _, _, m, _ -> CalendarScreen(modifier = m) },
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
        compose = { _, _, m, _ -> DecorationScreen(modifier = m) },
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
        compose = { backstack, controller, modifier, resolveOptions ->
            FriendScreen(
                controller = controller,
                model = backstack.hiltViewModel<FriendsViewModel>(controller),
                modifier = modifier,
                resolveOptions = resolveOptions
            )
        },
        // Options [Route.Node.options] are not defined here, because we are not using static values
        // of this route anywhere
        resolveOptions = { backstack, controller ->
            val state by backstack
                .hiltViewModel<FriendsViewModel>(controller).stateFriend
                .collectAsStateWithLifecycle()

            val title = with(state.entry.user.username) {
                if (isNotBlank()) {
                    stringResource(
                        R.string.route_friend,
                        this
                    )
                } else {
                    ""
                }
            }

            Options(title = { RouteTitle(title) })
        },
        path = "friend"
    )

    data object Friends : Node(
        compose = { backstack, controller, modifier, _ ->
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

    data object None : Node(
        compose = { _, _, _, _ -> },
        path = "none"
    )

    data object Settings : Node(
        compose = { _, _, m, _ -> SettingsScreen(modifier = m) },
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

    @SuppressLint("NewApi")
    data object Steps : Node(
        compose = { _, _, m, _ -> StepScreen(modifier = m) },
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
        compose = { _, _, _, _ -> SurveyScreen() },
        options = Options(
            title = { RouteTitle(stringResource(R.string.route_survey)) }
        ),
        path = "survey"
    )
}