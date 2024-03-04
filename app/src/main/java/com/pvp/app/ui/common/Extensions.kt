package com.pvp.app.ui.common

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * Extension function to navigate to a route. This function will pop up to the start destination
 * of the graph before navigating to the new route. This is to ensure that the back stack is cleared.
 * State is saved and restored to ensure that the back stack is not lost.
 *
 * @param route The route to navigate to.
 */
fun @Composable NavHostController.navigateTo(route: String) {
    val graph = this.graph

    navigate(
        route
    ) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}
