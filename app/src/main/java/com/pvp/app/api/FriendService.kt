package com.pvp.app.api

import com.pvp.app.model.FriendObject
import kotlinx.coroutines.flow.Flow

interface FriendService : DocumentsCollection {

    override val identifier: String
        get() = "friends"

    /**
     * Gets FriendObject by its email. In case it is not found, null is returned.
     */
    suspend fun get(email: String): Flow<FriendObject?>

    /**
     * Creates or updates the FriendObject in the database.
     */
    suspend fun merge(
        friendObject: FriendObject,
        email: String
    )

    /**
     * Removes an FriendObject from the database with all associated children data.
     */
    suspend fun remove(email: String)

    /**
     * Removes a friend from the friend list of the current user and vice versa.
     *
     * @param friendObject of the current user.
     * @param friendObjectEmail of the current user.
     * @param friendEmail of the friend to be removed.
     * @return a string message indicating the result of the operation.
     */
    suspend fun removeFriend(
        friendObject: FriendObject,
        friendObjectEmail: String,
        friendEmail: String
    )

    /**
     * IMPORTANT!!! This method returns a string that is strictly associated with how ViewModel works.
     * IMPORTANT!!! If any changes to the default implementation are made, changes in the ViewModel should also be made.
     *
     * Sends a friend request from the current user to another user.
     *
     * @param friendObject of the current user.
     * @param email of the current user.
     * @param friendEmail of the user to whom the friend request is being sent.
     * @return a string message indicating the result of the operation.
     */
    suspend fun addFriend(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String

    /**
     * Accepts a friend request from another user.
     *
     * @param friendObject of the current user.
     * @param email of the current user.
     * @param friendEmail of the user whose friend request is being accepted.
     * @return a string message indicating the result of the operation.
     */
    suspend fun acceptFriendRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String

    /**
     * Denies a friend request from another user.
     *
     * @param friendObject of the current user.
     * @param email of the current user.
     * @param friendEmail of the user whose friend request is being denied.
     * @return a string message indicating the result of the operation.
     */
    suspend fun denyFriendRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String

    /**
     * Cancels a friend request that the current user has sent to another user.
     *
     * @param friendObject of the current user.
     * @param email of the current user.
     * @param friendEmail of the user to whom the friend request was sent.
     * @return a string message indicating the result of the operation.
     */
    suspend fun cancelSentRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String
}