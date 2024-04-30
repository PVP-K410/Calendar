package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pvp.app.ui.common.LocalBackgroundColors
import com.pvp.app.ui.common.LocalHorizontalPagerSettled
import com.pvp.app.ui.common.LocalRouteOptions
import com.pvp.app.ui.common.LocalRouteOptionsApplier
import com.pvp.app.ui.common.backgroundGradientVertical
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.router.Routes

@Composable
fun LayoutScreenUnauthenticated(
    areSurveysFilled: Boolean?,
    controller: NavHostController,
    isAuthenticated: Boolean
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        CompositionLocalProvider(
            LocalHorizontalPagerSettled provides true,
            LocalRouteOptions provides Route.Options.None,
            LocalRouteOptionsApplier provides { },
        ) {
            Router(
                controller = controller,
                start = if (isAuthenticated && areSurveysFilled == false) {
                    Routes.Survey
                } else {
                    Routes.Authentication
                },
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundGradientVertical(LocalBackgroundColors.current),
                routes = Routes.routesUnauthenticated,
            )
        }
    }
}