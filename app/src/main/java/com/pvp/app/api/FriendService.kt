package com.pvp.app.api

import com.pvp.app.model.FriendObject
import kotlinx.coroutines.flow.Flow

interface FriendService : DocumentsCollection {

    override val identifier: String
        get() = "friendObjects"

    /**
     * Gets FriendObject by its email. In case it is not found, null is returned.
     */
    suspend fun get(email: String): Flow<FriendObject?>

    /**
     * Creates or updates the FriendObject in the database.
     */
    suspend fun merge(friendObject: FriendObject, userEmail: String)

    /**
     * Removes an FriendObject from the database with all associated children data.
     */
    suspend fun remove(email: String)

    /**
     * Creates a new FriendObject for a given email if it does not already exist.
     *
     * @param email for which to create a FriendObject.
     */
    suspend fun createFriendObject(email: String)

    /**
     * Sends a friend request from the current user to another user.
     *
     * @param friendObject of the current user.
     * @param userEmail of the current user.
     * @param friendEmail of the user to whom the friend request is being sent.
     * @return a string message indicating the result of the operation.
     */
    suspend fun addFriend(
        friendObject: FriendObject,
        userEmail: String,
        friendEmail: String
    ): String

    /**
     * Accepts a friend request from another user.
     *
     * @param friendObject of the current user.
     * @param userEmail of the current user.
     * @param friendEmail of the user whose friend request is being accepted.
     * @return a string message indicating the result of the operation.
     */
    suspend fun acceptFriendRequest(
        friendObject: FriendObject,
        userEmail: String,
        friendEmail: String
    ): String

    /**
     * Denies a friend request from another user.
     *
     * @param friendObject of the current user.
     * @param userEmail of the current user.
     * @param friendEmail of the user whose friend request is being denied.
     * @return a string message indicating the result of the operation.
     */
    suspend fun denyFriendRequest(
        friendObject: FriendObject,
        userEmail: String,
        friendEmail: String
    ): String

    /**
     * Cancels a friend request that the current user has sent to another user.
     *
     * @param friendObject of the current user.
     * @param userEmail of the current user.
     * @param friendEmail of the user to whom the friend request was sent.
     * @return a string message indicating the result of the operation.
     */
    suspend fun cancelSentRequest(
        friendObject: FriendObject,
        userEmail: String,
        friendEmail: String
    ): String
}