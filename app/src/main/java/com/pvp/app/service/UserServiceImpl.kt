@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Provider

class UserServiceImpl @Inject constructor(
    private val authenticationServiceProvider: Provider<AuthenticationService>,
    private val database: FirebaseFirestore,
    private val decorationService: DecorationService
) : UserService {

    override val user
        get() = authenticationServiceProvider
            .get().user
            .flatMapLatest { user ->
                user?.email
                    ?.run { get(this) }
                    ?: flowOf(null)
            }

    override suspend fun get(email: String): Flow<User?> {
        val flow = database
            .collection(identifier)
            .document(email)
            .snapshots()
            .mapLatest { it.toObject(User::class.java) }

        return flow.combine(decorationService.getAvatar(flow)) { user, avatar ->
            user?.apply { this.avatar = avatar }
        }
    }

    override suspend fun merge(user: User) {
        database
            .runTransaction { transaction ->
                val document = database
                    .collection(identifier)
                    .document(user.email)

                transaction.set(
                    document,
                    user.apply { avatar = null }
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
}