@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.api.Configuration
import com.pvp.app.api.DecorationService
import com.pvp.app.api.ImageService
import com.pvp.app.api.UserService
import com.pvp.app.model.Decoration
import com.pvp.app.model.Type
import com.pvp.app.model.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Provider

class DecorationServiceImpl @Inject constructor(
    private val configuration: Configuration,
    private val database: FirebaseFirestore,
    private val imageService: ImageService,
    private val userServiceProvider: Provider<UserService>
) : DecorationService {

    override suspend fun apply(
        decoration: Decoration,
        image: ImageBitmap
    ): ImageBitmap {
        val bitmapBase = image
            .asAndroidBitmap()
            .copy(
                Bitmap.Config.ARGB_8888,
                true
            )

        val bitmapDecoration = imageService
            .get(decoration.imageLayerUrl)
            .asAndroidBitmap()
            .resize(
                bitmapBase.width,
                bitmapBase.height
            )

        val canvas = Canvas(bitmapBase)

        canvas.drawBitmap(
            bitmapDecoration,
            0f,
            0f,
            null
        )

        return bitmapBase.asImageBitmap()
    }

    override suspend fun apply(
        decoration: Decoration,
        user: User
    ) {
        if (decoration.id !in user.decorationsOwned) {
            error("${user.username} does not own ${decoration.id} decoration. Cannot apply.")
        }

        val decorations = user.decorationsApplied.toMutableList()

        if (decoration.id in user.decorationsApplied) {
            if (isDefault(decoration)) {
                error("Cannot remove default decoration ${decoration.id}")
            }

            decorations.remove(decoration.id)
        } else {
            decorations.add(decoration.id)
        }

        userServiceProvider
            .get()
            .merge(user.copy(decorationsApplied = decorations))
    }

    override suspend fun get(): Flow<List<Decoration>> {
        return database
            .collection(identifier)
            .snapshots()
            .mapLatest { snapshot ->
                snapshot.documents.mapNotNull { document ->
                    document.toObject(Decoration::class.java)
                }
            }
    }

    override suspend fun get(id: String): Flow<Decoration> {
        return database
            .collection(identifier)
            .document(id)
            .snapshots()
            .mapLatest { snapshot -> snapshot.toObject(Decoration::class.java) }
            .filterNotNull()
    }

    override suspend fun getAvatar(user: Flow<User?>): Flow<ImageBitmap> {
        return user.mapLatest { userLatest ->
            var avatar = imageService.getOrDefault(configuration.imageUrlDefaultAvatar)

            if (userLatest == null) {
                return@mapLatest avatar
            }

            userLatest.decorationsApplied
                .mapNotNull {
                    get(it)
                        .firstOrNull()
                }
                .sortedWith(
                    compareBy<Decoration> { isDefault(it) }
                        .thenBy { it.type == Type.AVATAR_HANDS }
                        .thenBy { it.type == Type.AVATAR_BODY }
                        .thenBy { it.type == Type.AVATAR_FACE }
                        .thenBy { it.type == Type.AVATAR_HEAD }
                        .thenBy { it.type == Type.AVATAR_LEGGINGS }
                        .thenBy { it.type == Type.AVATAR_SHOES }
                )
                .forEach {
                    avatar = apply(
                        it,
                        avatar
                    )
                }

            avatar
        }
    }

    override suspend fun merge(decoration: Decoration) {
        database
            .runTransaction { transaction ->
                database
                    .collection(identifier)
                    .document(decoration.id)
                    .let { document ->
                        transaction.set(
                            document,
                            decoration
                        )
                    }
            }
            .await()
    }

    override suspend fun purchase(
        decoration: Decoration,
        user: User
    ) {
        if (user.points < decoration.price) {
            error("${user.username} has insufficient amount of points to purchase ${decoration.id} decoration")
        }

        userServiceProvider
            .get()
            .merge(
                user.apply {
                    decorationsOwned += decoration.id
                    points -= decoration.price
                }
            )
    }

    override suspend fun remove(decoration: Decoration) {
        database
            .runTransaction { transaction ->
                database
                    .collection(identifier)
                    .document(decoration.id)
                    .let { document -> transaction.delete(document) }
            }
            .await()
    }

    companion object {

        private fun Bitmap.resize(
            width: Int,
            height: Int
        ): Bitmap {
            return Bitmap.createScaledBitmap(
                this,
                width,
                height,
                true
            )
        }
    }
}