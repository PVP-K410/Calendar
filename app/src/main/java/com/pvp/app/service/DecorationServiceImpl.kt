@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import com.pvp.app.R
import com.pvp.app.api.DecorationService
import com.pvp.app.api.UserService
import com.pvp.app.common.ImageUtil.toImageBitmap
import com.pvp.app.model.Decoration
import com.pvp.app.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class DecorationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val userServiceProvider: Provider<UserService>
) : DecorationService {

    override suspend fun apply(
        decoration: Decoration,
        image: ImageBitmap
    ): ImageBitmap {
        return image
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

    override suspend fun getAvatar(user: Flow<User?>): Flow<ImageBitmap> {
        return user
            .mapNotNull { it }
            .mapLatest {
                try {
                    SVG
                        .getFromResource(
                            context.resources,
                            R.raw.avatar
                        )
                        .renderToPicture()
                        .toImageBitmap()
                } catch (e: SVGParseException) {
                    Log.e(
                        "DecorationService",
                        "Failed to resolve user avatar: ${e.message}"
                    )

                    ImageBitmap(
                        1,
                        1
                    )
                }
            }
    }

    override suspend fun getImage(decoration: Decoration): ImageBitmap =
        withContext(Dispatchers.IO) {
            val extension = decoration.imageUrl.substringAfterLast('.')

            if (extension != "svg") {
                error("Unsupported image format: .$extension")
            }

            var file = findSvgFile(
                context,
                decoration
            )

            if (file != null) {
                if (
                    System.currentTimeMillis() - file.lastModified() >
                    3.toDuration(DurationUnit.DAYS).inWholeMilliseconds
                ) {
                    file.delete()

                    file = downloadSvgFile(
                        context,
                        decoration,
                        storage
                    )
                }

                return@withContext SVG
                    .getFromInputStream(file.inputStream())
                    .renderToPicture()
                    .toImageBitmap()
            }

            file = downloadSvgFile(
                context,
                decoration,
                storage
            )

            try {
                SVG
                    .getFromInputStream(file.inputStream())
                    .renderToPicture()
                    .toImageBitmap()
            } catch (e: Exception) {
                Log.e(
                    "DecorationService",
                    "Failed to resolve decoration image: ${e.message}"
                )

                ImageBitmap(
                    1,
                    1
                )
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

        private suspend fun downloadSvgFile(
            context: Context,
            decoration: Decoration,
            storage: FirebaseStorage
        ): File = withContext(Dispatchers.IO) {
            val file = File(
                context.filesDir,
                "${decoration.id}.svg"
            )

            storage
                .getReferenceFromUrl(decoration.imageUrl)
                .getFile(file)
                .await()

            file.absoluteFile
        }

        private suspend fun findSvgFile(
            context: Context,
            decoration: Decoration
        ): File? = withContext(Dispatchers.IO) {
            val path = context.filesDir
                .toPath()
                .resolve(decoration.id + ".svg")

            if (Files.exists(path)) {
                path.toFile()
            } else {
                null
            }
        }
    }
}