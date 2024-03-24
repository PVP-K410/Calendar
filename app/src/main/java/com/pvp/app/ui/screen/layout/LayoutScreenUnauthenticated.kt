package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pvp.app.ui.common.LocalBackgroundColors
import com.pvp.app.ui.common.backgroundGradientVertical
import com.pvp.app.ui.router.Route
import com.pvp.app.ui.router.Router
import kotlinx.coroutines.CoroutineScope

@Composable
fun LayoutScreenUnauthenticated(
    areSurveysFilled: Boolean?,
    controller: NavHostController,
    isAuthenticated: Boolean,
    scope: CoroutineScope
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Router(
            controller = controller,
            destinationStart = if (isAuthenticated && areSurveysFilled == false) {
                Route.Survey
            } else {
                Route.Authentication
            },
            modifier = Modifier
                .fillMaxSize()
                .backgroundGradientVertical(LocalBackgroundColors.current),
            routes = Route.routesUnauthenticated,
            scope = scope
        )
    }
}