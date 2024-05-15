package com.pvp.app.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.pvp.app.ui.common.lighten

val BackgroundGradientDefault = listOf(
    Color("#FF5A6148".toColorInt()),
    Color("#FF396660".toColorInt())
)

val BackgroundGradientSunset = listOf(
    Color.Yellow.lighten(0.5f),
    Color.Red.lighten(0.5f),
    Color.White
)

val ButtonTransparent = ButtonColors(
    containerColor = Color.Transparent,
    contentColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    disabledContentColor = Color.Transparent
)

val DarkPrimary = Color(0xFFACD458)
val DarkSecondary = Color(0xFFC2CAAB)
val DarkTertiary = Color(0xFFA0D0C8)

val LightPrimary = Color(0xFF4B6700)
val LightSecondary = Color(0xFF5A6148)
val LightTertiary = Color(0xFF396660)
val LightSurfaceContainer = Color(0xFFDADBCF)
