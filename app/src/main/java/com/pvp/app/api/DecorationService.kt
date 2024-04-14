package com.pvp.app.api

import androidx.compose.ui.graphics.ImageBitmap
import com.pvp.app.model.Decoration
import com.pvp.app.model.User
import kotlinx.coroutines.flow.Flow

interface DecorationService : DocumentsCollection {

    override val identifier: String
        get() = "decorations"

    /**
     * Apply a decoration to an image.
     *
     * @param image The image to apply the decoration to.
     * @param decoration The decoration to apply.
     *
     * @return The decorated image.
     */
    suspend fun apply(
        image: ImageBitmap,
        decoration: Decoration
    ): ImageBitmap

    /**
     * Get all decorations.
     */
    suspend fun get(): Flow<List<Decoration>>

    /**
     * Gets decorated version of user's avatar.
     *
     * @param user The user to get the avatar for.
     */
    suspend fun getAvatar(user: Flow<User?>): Flow<ImageBitmap>

    /**
     * Add a new decoration to the database or update an existing one.
     *
     * @param decoration The decoration to add or update.
     */
    suspend fun merge(decoration: Decoration)

    /**
     * Purchase a decoration for a user.
     *
     * @param decoration The decoration to purchase.
     * @param user The user to purchase the decoration for.
     */
    suspend fun purchase(
        decoration: Decoration,
        user: User
    )

    /**
     * @param decoration The decoration to remove.
     *
     * Remove a decoration from the database.
     */
    suspend fun remove(decoration: Decoration)
}