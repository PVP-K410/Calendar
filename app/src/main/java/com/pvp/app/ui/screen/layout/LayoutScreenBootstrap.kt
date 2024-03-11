package com.pvp.app.ui.screen.layout

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.pvp.app.ui.common.ProgressIndicator

@Composable
@SuppressLint("StateFlowValueCalledInComposition")
fun LayoutScreenBootstrap(
    viewModel: LayoutViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        ProgressIndicator()

        return
    }

    if (state.userFirebase.value == null) {
        LayoutScreenUnauthenticated(
            controller = rememberNavController(),
            scope = rememberCoroutineScope()
        )

        return
    }

    LayoutScreenAuthenticated(
        controller = rememberNavController(),
        scope = rememberCoroutineScope(),
        user = state.userApp.value
    )
}