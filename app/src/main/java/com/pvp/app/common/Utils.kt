package com.pvp.app.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.drawable.PictureDrawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.model.SportActivity
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId

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

fun List<ExerciseSessionRecord>.toSportActivities(): List<SportActivity> {
    return this.mapNotNull { exercise ->
        SportActivity.fromId(exercise.exerciseType)
    }
}

fun List<SportActivity>.getOccurences(): List<Pair<SportActivity, Int>> {
    return this
        .groupingBy { it }
        .eachCount()
        .toList()
        .sortedByDescending { it.second }
}

fun LocalDateTime.resetTime(): LocalDateTime {
    return withHour(0)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
}

fun LocalDateTime.toEpochSecondTimeZoned(): Long {
    return toEpochSecond(
        ZoneId
            .systemDefault().rules
            .getOffset(this)
    )
}