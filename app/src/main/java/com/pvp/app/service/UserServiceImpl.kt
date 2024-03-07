package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Provider

class UserServiceImpl @Inject constructor(
    private val authenticationServiceProvider: Provider<AuthenticationService>,
    private val database: FirebaseFirestore
) : UserService {

    override suspend fun get(email: String): Flow<User?> {
        return database
            .collection(identifier)
            .document(email)
            .snapshots()
            .map { it.toObject(User::class.java) }
    }

    override suspend fun getCurrent(): Flow<User?> {
        return authenticationServiceProvider
            .get().user
            .firstOrNull()
            ?.run { email?.run { get(this) } }
            ?: flowOf(null)
    }

    override suspend fun merge(user: User) {
        database
            .collection(identifier)
            .document(user.email)
            .set(user)
            .await()
    }

    override suspend fun remove(email: String) {
        database
            .collection(identifier)
            .document(email)
            .delete()
            .await()
    }
}