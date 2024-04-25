package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.common.LocalBackgroundColors
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.backgroundGradientVertical
import com.pvp.app.ui.router.Routes
import com.pvp.app.ui.theme.BackgroundGradientSunset

/**
 * This function initializes routes for the app, since they are singletons and
 * are not initialized until the first time they are accessed.
 */
private fun initializeRouteSingleton() {
    Routes.routesAuthenticated
    Routes.routesUnauthenticated
    Routes.routesDrawer
}

@Composable
fun LayoutScreenBootstrap(model: LayoutViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { initializeRouteSingleton() }

    CompositionLocalProvider(LocalBackgroundColors provides BackgroundGradientSunset) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .backgroundGradientVertical(LocalBackgroundColors.current)
        ) {
            val state by model.state.collectAsStateWithLifecycle()

            when {
                state.isLoading -> {
                    ProgressIndicator()
                }

                !state.isAuthenticated || state.areSurveysFilled == false -> {
                    LayoutScreenUnauthenticated(
                        areSurveysFilled = state.areSurveysFilled,
                        controller = rememberNavController(),
                        isAuthenticated = state.isAuthenticated
                    )
                }

                else -> {
                    LayoutScreenAuthenticated(controller = rememberNavController())
                }
            }
        }
    }
}