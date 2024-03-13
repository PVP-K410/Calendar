package com.pvp.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pvp.app.ui.theme.BackgroundGradientDefault

private fun colorsToPairs(
    colors: List<Color>,
    radius: Float = 1f
): Array<Pair<Float, Color>> {
    return (if (colors.size >= 2) colors else BackgroundGradientDefault)
        .mapIndexed { index, color ->
            (index.toFloat() / (colors.size - 1)) * radius to color
        }
        .toTypedArray()
}

fun Modifier.backgroundGradientRadial(
    colors: List<Color> = BackgroundGradientDefault,
    radius: Float = 1500.0f,
    tileMode: TileMode = TileMode.Repeated
): Modifier {
    return background(
        Brush.radialGradient(
            colorStops = colorsToPairs(colors, radius),
            radius = radius,
            tileMode = tileMode
        )
    )
}

fun Modifier.backgroundGradientLinear(
    colors: List<Color> = BackgroundGradientDefault,
    end: Offset = Offset.Infinite,
    start: Offset = Offset.Zero
): Modifier {
    return background(
        Brush.linearGradient(
            colorStops = colorsToPairs(colors),
            start = start,
            end = end
        )
    )
}

fun Modifier.backgroundGradientHorizontal(
    colors: List<Color> = BackgroundGradientDefault,
    end: Float = 1000.0f,
    start: Float = 0.0f
): Modifier {
    return background(
        Brush.horizontalGradient(
            colorStops = colorsToPairs(colors),
            startX = start,
            endX = end
        )
    )
}

fun Modifier.backgroundGradientVertical(
    colors: List<Color> = BackgroundGradientDefault,
    end: Float = 1500.0f,
    start: Float = 0.0f
): Modifier {
    return background(
        Brush.verticalGradient(
            colorStops = colorsToPairs(colors),
            startY = start,
            endY = end
        )
    )
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(
            brush = brush,
            blendMode = BlendMode.DstIn
        )
    }

fun Modifier.underline(): Modifier = this
    .drawBehind {
        val verticalOffset = size.height - 2.sp.toPx()

        drawLine(
            color = Color.Black,
            strokeWidth = 1.dp.toPx(),
            start = Offset(0f, verticalOffset),
            end = Offset(size.width, verticalOffset)
        )
    }