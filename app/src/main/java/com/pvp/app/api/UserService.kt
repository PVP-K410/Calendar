package com.pvp.app.api

import androidx.compose.ui.graphics.ImageBitmap
import com.pvp.app.model.User
import kotlinx.coroutines.flow.Flow

interface UserService : DocumentsCollection {

    override val identifier: String
        get() = "users"

    /**
     * Gets user by its email. In case it is not found, null is returned.
     */
    suspend fun get(email: String): Flow<User?>

    /**
     * Gets current application user. In case it cannot be found or resolved, null is returned.
     */
    suspend fun getCurrent(): Flow<User?>

    /**
     * Creates or updates the user in the database.
     */
    suspend fun merge(user: User)

    /**
     * Removes an user from the database with all associated children data.
     */
    suspend fun remove(email: String)

    /**
     * Resolves the avatar of the user by its email. User can have decorated avatar, which requires
     * to be resolved by this method.
     *
     * @param email of the user
     * @return the avatar of the user
     */
    suspend fun resolveAvatar(email: String): ImageBitmap
}