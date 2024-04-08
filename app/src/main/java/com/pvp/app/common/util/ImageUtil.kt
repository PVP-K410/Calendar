package com.pvp.app.common.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

object ImageUtil {

    /**
     * Converts a Picture to an ImageBitmap
     */
    fun Picture.toImageBitmap(): ImageBitmap {
        PictureDrawable(this)
            .run {
                val bitmap = Bitmap.createBitmap(
                    intrinsicWidth,
                    intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )

                val canvas = Canvas(bitmap)

                setBounds(
                    0,
                    0,
                    canvas.width,
                    canvas.height
                )

                draw(canvas)

                return bitmap.asImageBitmap()
            }
    }
}