package com.pvp.app.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.time.Duration

/**
 * Parses Duration object to a string
 * Format <HH> h <mm> min
 */
fun getDurationString(duration: Duration): String {
    val hours = duration.toHours()
    val minutes = duration.minusHours(hours).toMinutes()

    return buildString {
        if (hours > 0) {
            append("$hours h")
        }

        if (minutes > 0) {
            append("${if (hours > 0) " " else ""}$minutes min")
        }
    }
}

fun Picture.toImageBitmap(): ImageBitmap {
    PictureDrawable(this)
        .run {
            val bitmap = Bitmap.createBitmap(
                intrinsicWidth,
                intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)

            setBounds(0, 0, canvas.width, canvas.height)

            draw(canvas)

            return bitmap.asImageBitmap()
        }
}