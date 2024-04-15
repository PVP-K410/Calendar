@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.R
import com.pvp.app.api.DecorationService
import com.pvp.app.api.UserService
import com.pvp.app.common.ImageUtil.toImageBitmap
import com.pvp.app.model.Decoration
import com.pvp.app.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Provider

class DecorationServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FirebaseFirestore,
    private val userServiceProvider: Provider<UserService>
) : DecorationService {

    override suspend fun apply(
        image: ImageBitmap,
        decoration: Decoration
    ): ImageBitmap {
        return image
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

    override suspend fun getImage(decoration: Decoration): ImageBitmap {
        return ImageBitmap(
            1,
            1
        )
    }

    override suspend fun merge(decoration: Decoration) {
        database
            .collection(identifier)
            .document(decoration.id)
            .set(decoration)
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
            .collection(identifier)
            .document(decoration.id)
            .delete()
            .await()
    }
}