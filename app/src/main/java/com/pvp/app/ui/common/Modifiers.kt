package com.pvp.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt


fun Modifier.backgroundGradient(
    colors: List<Color> = listOf(
        Color("#FFACD458".toColorInt()),
        Color("#FFC2CAAB".toColorInt())
    ),
    offsetStart: Offset = Offset.Zero
): Modifier {
    return background(
        Brush.radialGradient(
            colors = colors,
            center = offsetStart
        )
    )
}