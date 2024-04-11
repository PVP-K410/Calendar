package com.pvp.app.api

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
}