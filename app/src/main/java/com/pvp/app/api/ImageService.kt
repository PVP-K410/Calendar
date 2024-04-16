package com.pvp.app.api

import androidx.compose.ui.graphics.ImageBitmap

interface ImageService {

    /**
     * @return image from [url].
     */
    suspend fun get(url: String): ImageBitmap

    /**
     * @return image from [url] or [default] if failed to resolve.
     */
    suspend fun getOrDefault(
        url: String,
        default: ImageBitmap = ImageBitmap(
            1,
            1
        )
    ): ImageBitmap {
        return try {
            get(url)
        } catch (e: Exception) {
            default
        }
    }

    suspend fun getOrNull(url: String): ImageBitmap? {
        return try {
            get(url)
        } catch (e: Exception) {
            null
        }
    }
}