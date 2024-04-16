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
     * @param decoration The decoration to apply.
     * @param image The image to apply the decoration to.
     *
     * @return The decorated image.
     */
    suspend fun apply(
        decoration: Decoration,
        image: ImageBitmap
    ): ImageBitmap

    /**
     * Apply a decoration for the specified user. All this should do is update user's applied
     * decorations and persist the user. In case user has already applied the decoration, the
     * decoration will be un-applied.
     *
     * @param decoration The decoration to (un)apply.
     * @param user The user to (un)apply the decoration for.
     */
    suspend fun apply(
        decoration: Decoration,
        user: User
    )

    /**
     * Get all decorations.
     *
     * @return A flow of all existing decorations.
     */
    suspend fun get(): Flow<List<Decoration>>

    /**
     * Gets decorated version of user's avatar.
     *
     * @param user The user to get the avatar for.
     *
     * @return A flow of the decorated avatar. In case user is null, returns a default avatar.
     */
    suspend fun getAvatar(user: Flow<User?>): Flow<ImageBitmap>

    /**
     * Get the image for a decoration.
     *
     * @param decoration The decoration to get the image for with [Decoration.imageUrl]
     *
     * @return The image for the decoration.
     */
    suspend fun getImage(decoration: Decoration): ImageBitmap

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