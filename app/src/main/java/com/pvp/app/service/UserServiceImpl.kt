package com.pvp.app.service

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.R
import com.pvp.app.api.AuthenticationService
import com.pvp.app.api.UserService
import com.pvp.app.common.JSON
import com.pvp.app.common.toImageBitmap
import com.pvp.app.common.toJsonElement
import com.pvp.app.common.toPrimitivesMap
import com.pvp.app.model.SportTask
import com.pvp.app.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject
import javax.inject.Provider

class UserServiceImpl @Inject constructor(
    private val authenticationServiceProvider: Provider<AuthenticationService>,
    @ApplicationContext
    private val context: Context,
    private val database: FirebaseFirestore
) : UserService {

    @OptIn(ExperimentalCoroutinesApi::class)
    override val user
        get() = authenticationServiceProvider
            .get().user
            .flatMapConcat {
                it?.email
                    ?.run { get(this) }
                    ?: flowOf(null)
            }

    private fun decode(
        user: Map<String, Any?>
    ): User {
        val element = user.toJsonElement()

        if (element !is JsonObject) {
            error("User data is not a JSON object")
        }

        val tasks = if (element.containsKey(User::dailyTasks.name)) {
            JSON.decodeFromJsonElement<List<SportTask>>(element[User::dailyTasks.name]!!)
        } else {
            emptyList()
        }

        return JSON
            .decodeFromJsonElement<User>(element)
            .also { it.dailyTasks = tasks }
    }

    override suspend fun get(email: String): Flow<User?> {
        return database
            .collection(identifier)
            .document(email)
            .snapshots()
            .map { ds -> ds.data?.let { decode(it) } }
    }

    override suspend fun merge(user: User) {
        database
            .collection(identifier)
            .document(user.email)
            .set(encode(user))
            .await()
    }

    override suspend fun remove(email: String) {
        database
            .collection(identifier)
            .document(email)
            .delete()
            .await()
    }

    private fun encode(
        user: User
    ): Map<String, Any?> {
        return JSON
            .encodeToJsonElement(user)
            .toPrimitivesMap()
            .plus(
                User::dailyTasks.name to user.dailyTasks.map {
                    JSON
                        .encodeToJsonElement<SportTask>(it)
                        .toPrimitivesMap()
                }
            )
    }

    override suspend fun resolveAvatar(email: String): ImageBitmap {
        // TODO: In the future, we will resolve the avatar by checking user's bought decorations.
        // For now, we will just return a default avatar.
        return try {
            SVG
                .getFromResource(
                    context.resources,
                    R.raw.avatar
                )
                .renderToPicture()
                .toImageBitmap()
        } catch (e: SVGParseException) {
            e.printStackTrace()

            error("Failed to resolve user avatar")
        }
    }
}