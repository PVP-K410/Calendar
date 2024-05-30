package com.pvp.app.ui.screen.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.common.CenteredSnackbarHost
import com.pvp.app.ui.common.LocalBackgroundColors
import com.pvp.app.ui.common.LocalShowSnackbar
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.backgroundGradientVertical
import com.pvp.app.ui.router.Routes
import com.pvp.app.ui.theme.BackgroundGradientSunset
import kotlinx.coroutines.launch

/**
 * This function initializes routes for the app, since they are singletons and
 * are not initialized until the first time they are accessed.
 */
private fun initializeRouteSingleton() {
    Routes.authenticated
    Routes.unauthenticated
    Routes.drawer
}

@Composable
fun LayoutScreenBootstrap(model: LayoutViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) { initializeRouteSingleton() }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val showSnackbar: (String) -> Unit = { message ->
        scope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    CompositionLocalProvider(
        LocalBackgroundColors provides BackgroundGradientSunset,
        LocalShowSnackbar provides showSnackbar
    ) {
        Scaffold(
            snackbarHost = { CenteredSnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
}