package com.pvp.app.ui.common

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

/**
 * Extension function to navigate to a route. This function will pop up to the start destination
 * of the graph before navigating to the new route. This is to ensure that the back stack is cleared.
 * State is saved and restored to ensure that the back stack is not lost.
 *
 * @param route The route to navigate to.
 */
fun @Composable NavHostController.navigateWithPopUp(route: String) {
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

fun <T : Context> T.showToast(
    duration: Int = Toast.LENGTH_LONG,
    message: String
) {
    Toast
        .makeText(
            this,
            message,
            duration
        )
        .show()
}

fun <T : Context> T.showToast(
    duration: Int = Toast.LENGTH_LONG,
    isSuccess: Boolean,
    messageError: String,
    messageSuccess: String
) {
    Toast
        .makeText(
            this,
            if (isSuccess) messageSuccess else messageError,
            duration
        )
        .show()
}
