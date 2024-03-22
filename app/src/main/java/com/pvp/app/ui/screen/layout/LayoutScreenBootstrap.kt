package com.pvp.app.ui.screen.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.backgroundGradientVertical
import com.pvp.app.ui.theme.BackgroundUnauthenticated

@Composable
@SuppressLint("StateFlowValueCalledInComposition")
fun LayoutScreenBootstrap(
    viewModel: LayoutViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .backgroundGradientVertical(BackgroundUnauthenticated)
    ) {
        val state by viewModel.state.collectAsStateWithLifecycle()

        when {
            state.isLoading -> {
                ProgressIndicator()
            }

            !state.isAuthenticated || state.areSurveysFilled == false -> {
                LayoutScreenUnauthenticated(
                    areSurveysFilled = state.areSurveysFilled,
                    controller = rememberNavController(),
                    isAuthenticated = state.isAuthenticated,
                    scope = rememberCoroutineScope()
                )
            }

            else -> {
                LayoutScreenAuthenticated(
                    controller = rememberNavController(),
                    scope = rememberCoroutineScope()
                )
            }
        }
    }
}