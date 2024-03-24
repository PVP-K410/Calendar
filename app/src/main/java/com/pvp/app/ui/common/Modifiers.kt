package com.pvp.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
    shape: Shape = RectangleShape,
    tileMode: TileMode = TileMode.Repeated
): Modifier = this.background(
    brush = Brush.radialGradient(
        colorStops = colorsToPairs(colors, radius),
        radius = radius,
        tileMode = tileMode
    ),
    shape = shape
)

fun Modifier.backgroundGradientLinear(
    colors: List<Color> = BackgroundGradientDefault,
    end: Offset = Offset.Infinite,
    shape: Shape = RectangleShape,
    start: Offset = Offset.Zero
): Modifier = this.background(
    brush = Brush.linearGradient(
        colorStops = colorsToPairs(colors),
        end = end,
        start = start
    ),
    shape = shape
)

fun Modifier.backgroundGradientHorizontal(
    colors: List<Color> = BackgroundGradientDefault,
    end: Float = 1000.0f,
    shape: Shape = RectangleShape,
    start: Float = 0.0f
): Modifier = this.background(
    brush = Brush.horizontalGradient(
        colorStops = colorsToPairs(colors),
        endX = end,
        startX = start
    ),
    shape = shape
)

fun Modifier.backgroundGradientVertical(
    colors: List<Color> = BackgroundGradientDefault,
    end: Float = 1500.0f,
    shape: Shape = RectangleShape,
    start: Float = 0.0f
): Modifier = this.background(
    brush = Brush.verticalGradient(
        colorStops = colorsToPairs(colors),
        endY = end,
        startY = start
    ),
    shape = shape
)

fun Modifier.fadingEdge(
    brush: Brush
) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()

        drawRect(
            blendMode = BlendMode.DstIn,
            brush = brush
        )
    }

@Composable
fun Modifier.underline(
    color: Color = MaterialTheme.colorScheme.onPrimaryContainer
): Modifier = this.drawBehind {
    val verticalOffset = size.height - 2.sp.toPx()

    drawLine(
        color = color,
        end = Offset(
            size.width,
            verticalOffset
        ),
        start = Offset(
            0f,
            verticalOffset
        ),
        strokeWidth = 1.dp.toPx()
    )
}