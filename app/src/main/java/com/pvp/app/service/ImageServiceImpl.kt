package com.pvp.app.service

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ImageRequest
import com.pvp.app.api.ImageService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader
) : ImageService {

    override suspend fun get(url: String): ImageBitmap =
        withContext(Dispatchers.IO) {
            val result = imageLoader.execute(
                ImageRequest
                    .Builder(context)
                    .data(url)
                    .build()
            )

            result.drawable ?: error("Failed to load image from $url")

            result.drawable!!
                .run {
                    toBitmap(
                        minimumWidth,
                        minimumHeight
                    )
                }
                .asImageBitmap()
        }
}