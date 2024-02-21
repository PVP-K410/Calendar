package com.pvp.app.service

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.UserService
import com.pvp.app.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserServiceImpl @Inject constructor(
    private val database: FirebaseFirestore
) : UserService {

    override fun create(email: String, height: Int, mass: Int, username: String): User {
        return User(email, height, mass, 0, username)
    }

    override suspend fun get(username: String): Flow<User?> {
        return database
            .collection(identifier)
            .document(username)
            .snapshots()
            .map { it.toObject(User::class.java) }
    }

    override suspend fun getOrResolveCurrent(): Flow<User?> {
        return database
            .collection(identifier)
            // TODO:
            //  Implement the logic to get the current user when login process is implemented
            .document("current")
            .snapshots()
            .map {
                if (it.exists()) {
                    return@map it.toObject(User::class.java)!!
                }

                return@map null
            }
    }

    override suspend fun merge(user: User) {
        database
            .collection(identifier)
            .document(user.username)
            .set(user)
            .await()
    }

    override suspend fun remove(username: String) {
        database
            .collection(identifier)
            .document(username)
            .delete()
            .await()
    }
}