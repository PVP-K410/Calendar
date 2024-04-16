package com.pvp.app.ui.common

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavHostController
import coil.request.ImageRequest
import coil.size.Size

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

fun Color.darken(
    fraction: Float = 0.5f
): Color {
    return ColorUtils
        .blendARGB(
            toArgb(),
            Color.Black.toArgb(),
            fraction
        )
        .run { Color(this) }
}

fun Color.lighten(
    fraction: Float = 0.5f
): Color {
    return ColorUtils
        .blendARGB(
            toArgb(),
            Color.White.toArgb(),
            fraction
        )
        .run { Color(this) }
}

@Composable
fun requestImage(
    context: Context = LocalContext.current,
    size: Size = Size.ORIGINAL,
    url: String
): ImageRequest {
    return ImageRequest
        .Builder(context)
        .data(url)
        .size(size)
        .build()
}