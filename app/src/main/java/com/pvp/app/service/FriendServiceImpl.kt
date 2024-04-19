package com.pvp.app.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.FriendService
import com.pvp.app.api.UserService
import com.pvp.app.model.FriendObject
import com.pvp.app.model.Friends
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendServiceImpl @Inject constructor(
    private val userService: UserService,
    private val database: FirebaseFirestore
) : FriendService {

    override suspend fun get(email: String): Flow<FriendObject?> {
        return flow {
            val initialSnapshot = database
                .collection(identifier)
                .document(email)
                .get()
                .await()

            emit(initialSnapshot.toObject(FriendObject::class.java))

            database
                .collection(identifier)
                .document(email)
                .snapshots()
                .collect { snapshot ->
                    emit(snapshot.toObject(FriendObject::class.java))
                }
        }
    }

    override suspend fun merge(
        friendObject: FriendObject,
        email: String
    ) {
        database
            .runTransaction { transaction ->
                val document = database
                    .collection(identifier)
                    .document(email)

                transaction.set(
                    document,
                    friendObject
                )
            }
            .await()
    }

    override suspend fun remove(email: String) {
        database
            .collection(identifier)
            .document(email)
            .delete()
            .await()

        database
            .collection(identifier)
            .get()
            .await()
            .documents
            .forEach { userDoc ->
                var user: FriendObject? = null

                try {
                    user = userDoc.toObject(FriendObject::class.java)
                } catch (e: Exception) {
                    Log.e("FriendServiceImpl", "Error parsing user document", e)
                }

                if (user != null) {
                    val updatedFriends = user.friends.filter { it.email != email }
                    val updatedSentRequests = user.sentRequests - email
                    val updatedReceivedRequests = user.receivedRequests - email

                    if (updatedFriends != user.friends ||
                        updatedSentRequests != user.sentRequests ||
                        updatedReceivedRequests != user.receivedRequests
                    ) {
                        val updatedUser = FriendObject(
                            friends = updatedFriends,
                            sentRequests = updatedSentRequests,
                            receivedRequests = updatedReceivedRequests
                        )

                        merge(
                            updatedUser,
                            userDoc.id
                        )
                    }
                }
            }
    }

    override suspend fun removeFriend(
        friendObject: FriendObject,
        friendObjectEmail: String,
        friendEmail: String
    ) {
        val friendObjectFriendsNew = friendObject.friends.filter { it.email != friendEmail }
        val friendObjectNew = friendObject.copy(friends = friendObjectFriendsNew)

        merge(
            friendObjectNew,
            friendObjectEmail
        )

        val friendNew = get(friendEmail).first()
        val friendFriendsNew = friendNew?.friends?.filter { it.email != friendObjectEmail }

        friendNew?.let {
            val updatedFriendUser = it.copy(friends = friendFriendsNew ?: emptyList())

            merge(
                updatedFriendUser,
                friendEmail
            )
        }
    }

    override suspend fun addFriend(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String {
        if (email == friendEmail) {
            return "You are always your very best friend!"
        }

        val friend = get(friendEmail)
            .first()!!

        if (friendObject.friends.any { it.email == friendEmail }) {
            return "$friendEmail is already your friend"
        }

        if (friendEmail in friendObject.sentRequests) {
            return "Friend request already sent to $friendEmail"
        }

        if (friendEmail in friendObject.receivedRequests) {
            acceptFriendRequest(
                friendObject,
                email,
                friendEmail
            )

            return "Both of you want to be friends! Request accepted!"
        }

        val friendNew = friend.copy(
            receivedRequests = friend.receivedRequests + email
        )

        merge(
            friendNew,
            friendEmail
        )

        val friendObjectNew = friendObject.copy(
            sentRequests = friendObject.sentRequests + friendEmail
        )

        merge(
            friendObjectNew,
            email
        )

        return "Friend request sent!"
    }

    override suspend fun acceptFriendRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String {
        val friend = get(friendEmail)
            .first()
            ?: return "Friend not found"

        val friendNew = friend.copy(
            sentRequests = friend.sentRequests - email,
            friends = friend.friends + Friends(email)
        )

        merge(
            friendNew,
            friendEmail
        )

        val friendObjectNew = friendObject.copy(
            receivedRequests = friendObject.receivedRequests - friendEmail,
            friends = friendObject.friends + Friends(friendEmail)
        )

        merge(
            friendObjectNew,
            email
        )

        return "Friend request accepted!"
    }

    override suspend fun denyFriendRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String {
        val friend = get(friendEmail)
            .first()
            ?: return "Friend not found"

        val friendNew = friend.copy(
            sentRequests = friend.sentRequests - email
        )

        merge(
            friendNew,
            friendEmail
        )

        val friendObjectNew = friendObject.copy(
            receivedRequests = friendObject.receivedRequests - friendEmail
        )

        merge(
            friendObjectNew,
            email
        )

        return "Friend request denied!"
    }

    override suspend fun cancelSentRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String {
        val friend = get(friendEmail)
            .first()
            ?: return "Friend not found"

        val friendNew = friend.copy(
            receivedRequests = friend.receivedRequests - email
        )

        merge(
            friendNew,
            friendEmail
        )

        val friendObjectNew = friendObject.copy(
            sentRequests = friendObject.sentRequests - friendEmail
        )

        merge(
            friendObjectNew,
            email
        )

        return "Friend request cancelled!"
    }
}