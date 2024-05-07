package com.pvp.app.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvp.app.model.Setting
import com.pvp.app.ui.screen.settings.SettingsViewModel
import com.pvp.app.ui.screen.settings.Theme

fun Color.darken(fraction: Float = 0.5f): Color {
    return ColorUtils
        .blendARGB(
            toArgb(),
            Color.Black.toArgb(),
            fraction
        )
        .run { Color(this) }
}

fun Color.lighten(fraction: Float = 0.5f): Color {
    return ColorUtils
        .blendARGB(
            toArgb(),
            Color.White.toArgb(),
            fraction
        )
        .run { Color(this) }
}

@Composable
fun Color.orInDarkTheme(
    darkThemeColor: Color,
    model: SettingsViewModel = hiltViewModel()
): Color {
    val themeValue by model
        .get(Setting.Appearance.ApplicationTheme)
        .collectAsStateWithLifecycle()

    return if (themeValue == Theme.Dark.ordinal) darkThemeColor else this
}