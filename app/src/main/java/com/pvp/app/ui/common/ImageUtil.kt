package com.pvp.app.ui.common

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import coil.size.Size
import coil.transform.Transformation

object ImageUtil {

    @Composable
    fun requestImage(
        context: Context = LocalContext.current,
        size: Size = Size.ORIGINAL,
        transformations: List<Transformation> = emptyList(),
        url: String
    ): ImageRequest {
        return ImageRequest
            .Builder(context)
            .data(url)
            .diskCacheKey(url)
            .memoryCacheKey(url)
            .placeholderMemoryCacheKey(url)
            .size(size)
            .transformations(transformations)
            .build()
    }
}