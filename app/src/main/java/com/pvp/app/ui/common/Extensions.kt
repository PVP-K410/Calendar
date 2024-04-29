package com.pvp.app.ui.common

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.core.graphics.ColorUtils

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
fun Int.pixelsToDp() = with(LocalDensity.current) { toDp() }