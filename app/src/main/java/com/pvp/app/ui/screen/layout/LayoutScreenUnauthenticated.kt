package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.pvp.app.ui.common.LocalBackgroundColors
import com.pvp.app.ui.common.backgroundGradientVertical
import com.pvp.app.ui.router.Router
import com.pvp.app.ui.router.Routes

@Composable
fun LayoutScreenUnauthenticated(
    areSurveysFilled: Boolean?,
    controller: NavHostController,
    isAuthenticated: Boolean
) {
    Surface(modifier = Modifier.fillMaxSize()) {
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