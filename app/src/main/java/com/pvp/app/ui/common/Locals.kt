package com.pvp.app.ui.common

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalBackgroundColors = staticCompositionLocalOf<List<Color>> {
    error("No LocalBackgroundColors provided")
}