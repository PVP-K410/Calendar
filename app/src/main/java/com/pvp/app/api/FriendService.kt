package com.pvp.app.api

import androidx.compose.ui.graphics.ImageBitmap
import com.pvp.app.model.User

interface FriendService {

    /**
     * Sends a friend request from the current user to another user.
     *
     * @param user current application user.
     * @param friendEmail of the user to whom the friend request is being sent.
     * @return a string message indicating the result of the operation.
     */
    suspend fun addFriend(user: User, friendEmail: String): String

    /**
     * Accepts a friend request from another user.
     *
     * @param user current application user.
     * @param friendEmail of the user whose friend request is being accepted.
     * @return a string message indicating the result of the operation.
     */
    suspend fun acceptFriendRequest(user: User, friendEmail: String): String

    /**
     * Denies a friend request from another user.
     *
     * @param user current application user.
     * @param friendEmail of the user whose friend request is being denied.
     * @return a string message indicating the result of the operation.
     */
    suspend fun denyFriendRequest(user: User, friendEmail: String): String

    /**
     * Cancels a friend request that the current user has sent to another user.
     *
     * @param user current application user.
     * @param friendEmail of the user to whom the friend request was sent.
     * @return a string message indicating the result of the operation.
     */
    suspend fun cancelSentRequest(user: User, friendEmail: String): String

    /**
     * Retrieves the avatar of a friend.
     *
     * @param friendEmail of the friend whose avatar is to be retrieved.
     * @return avatar of the friend as an ImageBitmap.
     */
    suspend fun getFriendAvatar(friendEmail: String): ImageBitmap
}