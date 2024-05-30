package com.pvp.app.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pvp.app.R
import kotlinx.serialization.Serializable

@Serializable
data class Decoration(
    val id: String = "",
    val imageLayerUrl: String = "",
    val imageRepresentativeUrl: String = "",
    val name: String = "",
    val price: Int = 0,
    val type: Type = Type.AVATAR_FACE
)

enum class Type(@StringRes val titleId: Int) {
    AVATAR_ACCESSORY(R.string.decoration_type_accessory),
    AVATAR_BODY(R.string.decoration_type_body),
    AVATAR_FACE(R.string.decoration_type_face),
    AVATAR_HANDS(R.string.decoration_type_hands),
    AVATAR_HEAD(R.string.decoration_type_head),
    AVATAR_LEGGINGS(R.string.decoration_type_leggings),
    AVATAR_SHOES(R.string.decoration_type_shoes);

    val title: @Composable () -> String
        get() = { stringResource(titleId) }
}