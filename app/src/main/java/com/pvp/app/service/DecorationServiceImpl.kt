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
import com.pvp.app.common.FlowUtil.firstOr
import com.pvp.app.common.FlowUtil.flattenFlow
import com.pvp.app.model.Decoration
import com.pvp.app.model.Type
import com.pvp.app.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Provider

class DecorationServiceImpl @Inject constructor(
    private val configuration: Configuration,
    private val database: FirebaseFirestore,
    private val imageService: ImageService,
    private val userServiceProvider: Provider<UserService>
) : DecorationService {

    private var defaults = MutableStateFlow<List<Decoration>>(emptyList())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            initializeDefaults()
        }
    }

    private suspend fun initializeDefaults() {
        configuration.defaultDecorationIds
            .map { get(it) }
            .flattenFlow()
            .collect { decorations -> defaults.update { decorations } }
    }

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

        val decorations = user.decorationsApplied
            .map { get(it) }
            .flattenFlow()
            .firstOr(emptyList())
            .toMutableList()

        val current = decorations.firstOrNull { it.type == decoration.type }

        if (current != null) {
            decorations.remove(current)
        }

        if (current == null || current.id != decoration.id) {
            decorations.add(decoration)
        }

        userServiceProvider
            .get()
            .merge(user.copy(decorationsApplied = decorations.map { it.id }))
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

    override suspend fun getAvatar(user: Flow<User>): Flow<ImageBitmap> {
        return user.combine(defaults) { user, defaults ->
            var avatar = AVATAR

            val decorations = user.decorationsApplied
                .map { decoration -> get(decoration) }
                .flattenFlow()
                .firstOr(emptyList())

            decorations
                .plus(
                    defaults.filter { default ->
                        default.type !in decorations.map { it.type }
                    }
                )
                .sorted()
                .forEach {
                    avatar = apply(
                        it,
                        avatar
                    )
                }

            avatar
        }
    }

    override suspend fun getAvatar(user: User): ImageBitmap {
        var avatar = AVATAR

        val decorations = user.decorationsApplied
            .map { decoration -> get(decoration) }
            .flattenFlow()
            .firstOr(emptyList())

        val defaults = defaults.firstOr(emptyList())

        decorations
            .plus(
                defaults.filter { default ->
                    default.type !in decorations.map { it.type }
                }
            )
            .sorted()
            .forEach {
                avatar = apply(
                    it,
                    avatar
                )
            }

        return avatar
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

        private val AVATAR: ImageBitmap
            get() = ImageBitmap(
                700,
                920
            )

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

        private fun List<Decoration>.sorted(): List<Decoration> {
            return sortedWith(
                compareBy<Decoration> { it.type == Type.AVATAR_FACE }
                    .thenBy { it.type == Type.AVATAR_HEAD }
                    .thenBy { it.type == Type.AVATAR_SHOES }
                    .thenBy { it.type == Type.AVATAR_BODY }
                    .thenBy { it.type == Type.AVATAR_LEGGINGS }
                    .thenBy { it.type == Type.AVATAR_HANDS }
            )
        }
    }
}