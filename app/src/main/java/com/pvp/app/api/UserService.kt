package com.pvp.app.api

import com.pvp.app.model.User
import kotlinx.coroutines.flow.Flow

interface UserService : DocumentsCollection {

    override val identifier: String
        get() = "users"

    /**
     * Gets user by its username. In case it is not found, null is returned.
     */
    suspend fun get(username: String): Flow<User?>

    /**
     * Gets, if already resolved, or resolves a current user of the application and returns it.
     * In case of failure an exception is thrown.
     */
    suspend fun getOrResolveCurrent(): Flow<User?>

    /**
     * Creates or updates the user in the database.
     */
    suspend fun merge(user: User)

    /**
     * Removes an user from the database with all associated data.
     */
    suspend fun remove(username: String)
}