package com.pvp.app.service

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.R
import com.pvp.app.api.FriendService
import com.pvp.app.model.FriendObject
import com.pvp.app.model.Friends
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FriendServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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
                    Log.e(
                        "FriendServiceImpl",
                        "Error parsing user document",
                        e
                    )
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

        val friendNew = get(friendEmail)
            .first()

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
            return context.getString(R.string.friends_error_invite_self)
        }

        val friend = get(friendEmail)
            .first()!!

        if (friendObject.friends.any { it.email == friendEmail }) {
            return context.getString(
                R.string.friends_error_already_friend,
                friendEmail
            )
        }

        if (friendEmail in friendObject.sentRequests) {
            return context.getString(
                R.string.friends_error_already_sent_request,
                friendEmail
            )
        }

        if (friendEmail in friendObject.receivedRequests) {
            acceptFriendRequest(
                friendObject,
                email,
                friendEmail
            )

            return context.getString(R.string.friends_success_already_received_request)
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

        return context.getString(R.string.friends_success_sent)
    }

    override suspend fun acceptFriendRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String {
        val friend = get(friendEmail)
            .first()
            ?: return context.getString(R.string.friends_error_not_found)

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

        return context.getString(R.string.friends_success_request_accepted)
    }

    override suspend fun denyFriendRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String {
        val friend = get(friendEmail)
            .first()
            ?: return context.getString(R.string.friends_error_not_found)

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

        return context.getString(R.string.friends_success_request_denied)
    }

    override suspend fun cancelSentRequest(
        friendObject: FriendObject,
        email: String,
        friendEmail: String
    ): String {
        val friend = get(friendEmail)
            .first()
            ?: return context.getString(R.string.friends_error_not_found)

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

        return context.getString(R.string.friends_success_request_canceled)
    }
}