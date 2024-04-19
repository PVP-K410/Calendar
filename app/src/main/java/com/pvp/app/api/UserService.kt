package com.pvp.app.api

import com.pvp.app.model.User
import kotlinx.coroutines.flow.Flow

interface UserService : DocumentsCollection {

    override val identifier: String
        get() = "users"

    /**
     * @return current application user. In case it cannot be found or resolved, null is returned.
     */
    val user: Flow<User?>

    /**
     * Gets user by its email. In case it is not found, null is returned.
     */
    suspend fun get(email: String): Flow<User?>

    /**
     * Creates or updates the user in the database.
     */
    suspend fun merge(user: User)

    /**
     * Removes an user from the database with all associated children data.
     */
    suspend fun remove(email: String)
}