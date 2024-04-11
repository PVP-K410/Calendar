package com.pvp.app.service

import com.pvp.app.api.FriendService
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FriendServiceImpl @Inject constructor(
    private val userService: UserService
) : FriendService {
    override suspend fun addFriend(user: User, friendEmail: String): String {
        val email = user.email

        if (email == friendEmail) {
            return "You are always your very best friend!"
        }

        val friend = userService
            .get(friendEmail)
            .first()

        if (friend == null) {
            return "User with email $friendEmail does not exist"
        }

        if (friendEmail in user.friends) {
            return "$friendEmail is already your friend"
        }

        if (friendEmail in user.sentRequests) {
            return "Friend request already sent to $friendEmail"
        }
        if (friendEmail in user.receivedRequests) {
            return "Friend request already received from $friendEmail"
        }

        val friendNew = friend.copy(receivedRequests = friend.receivedRequests + email)

        userService.merge(friendNew)

        val userNew = user.copy(sentRequests = user.sentRequests + friendEmail)

        userService.merge(userNew)

        return "Friend request sent!"
    }

    override suspend fun acceptFriendRequest(user: User, friendEmail: String): String {
        val email = user.email

        val friend = userService
            .get(friendEmail)
            .first()
            ?: return "Friend not found"

        val friendNew = friend.copy(
            sentRequests = friend.sentRequests - email,
            friends = friend.friends + email
        )

        userService.merge(friendNew)

        val userNew = user.copy(
            receivedRequests = user.receivedRequests - friendEmail,
            friends = user.friends + friendEmail
        )

        userService.merge(userNew)

        return "Friend request accepted!"
    }

    override suspend fun denyFriendRequest(user: User, friendEmail: String): String {
        val email = user.email

        val friend = userService
            .get(friendEmail)
            .first()
            ?: return "Friend not found"

        val friendNew = friend.copy(sentRequests = friend.sentRequests - email)

        userService.merge(friendNew)

        val userNew = user.copy(receivedRequests = user.receivedRequests - friendEmail)

        userService.merge(userNew)

        return "Friend request denied!"
    }
}