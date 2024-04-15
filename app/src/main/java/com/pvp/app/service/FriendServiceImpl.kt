package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.FriendService
import com.pvp.app.api.UserService
import com.pvp.app.model.FriendObject
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
            .runTransaction { transaction ->
                val document = database
                    .collection(identifier)
                    .document(email)

                transaction.delete(document)
            }
            .await()
    }

    override suspend fun create(email: String) {
        val friendObject = get(email)
            .first()

        if (friendObject == null) {
            val newFriendObject = FriendObject(
                friends = emptyList(),
                receivedRequests = emptyList(),
                sentRequests = emptyList()
            )

            merge(
                newFriendObject,
                email
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

        if (friendEmail in friendObject.friends) {
            return "$friendEmail is already your friend"
        }

        if (friendEmail in friendObject.sentRequests) {
            return "Friend request already sent to $friendEmail"
        }

        if (friendEmail in friendObject.receivedRequests) {
            return "Friend request already received from $friendEmail"
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
            friends = friend.friends + email
        )

        merge(
            friendNew,
            friendEmail
        )

        val friendObjectNew = friendObject.copy(
            receivedRequests = friendObject.receivedRequests - friendEmail,
            friends = friendObject.friends + friendEmail
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