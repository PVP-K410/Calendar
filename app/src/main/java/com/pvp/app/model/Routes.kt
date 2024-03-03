package com.pvp.app.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope

interface Route {

    /**
     * The route of the screen. Prefer to path the route as typical website path, i.e.
     * "chat/{id}/as/{userId}".
     */
    val route: String
}

interface RouteComposeAuthenticated : Route {

    /**
     * Composable function that represents the screen of the route. Composable provides
     * authenticated controller, unauthenticated controller and the coroutine scope as
     * lambda parameters in the specified order.
     *
     * @param controllerAuthenticated The authenticated controller that supports routes
     * from [com.pvp.app.ui.router.RouteAuthenticated.routes]
     * @param controllerUnauthenticated The unauthenticated controller that supports
     * routes from [com.pvp.app.ui.router.RouteUnauthenticated.routes]
     * @param scope The coroutine scope.
     */
    val screen: @Composable (NavHostController, NavHostController, CoroutineScope) -> Unit
}

interface RouteComposeUnauthenticated : Route {

    /**
     * Composable function that represents the screen of the route. Composable provides
     * controller and the coroutine scope as lambda parameters in the specified order.
     *
     * @param controller The controller that supports routes from
     * [com.pvp.app.ui.router.RouteUnauthenticated.routes]
     * @param scope The coroutine scope.
     */
    val screen: @Composable (NavHostController, CoroutineScope) -> Unit
}

interface RouteShowcased {

    /**
     * The icon of the route.
     */
    val icon: ImageVector

    /**
     * The description of the icon. This is used for accessibility.
     */
    val iconDescription: String

    /**
     * The resource id of the title of the route.
     */
    val resourceTitleId: Int
}