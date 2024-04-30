package com.pvp.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

object RouteUtil {

    /**
     * If there is any routes in the backstack that had specified viewModel, it will be
     * returned. Otherwise, it will return the viewModel of the current route.
     */
    @Composable
    inline fun <reified T : ViewModel> NavBackStackEntry.hiltViewModel(
        controller: NavController
    ): T {
        val navGraphRoute = destination.parent?.route ?: return hiltViewModel()

        val parentEntry = remember(this) {
            controller.getBackStackEntry(navGraphRoute)
        }

        return hiltViewModel(parentEntry)
    }
}