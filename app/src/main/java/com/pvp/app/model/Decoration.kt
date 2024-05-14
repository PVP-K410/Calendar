package com.pvp.app.model

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

enum class Type {
    AVATAR_ACCESSORY,
    AVATAR_BODY,
    AVATAR_FACE,
    AVATAR_HANDS,
    AVATAR_HEAD,
    AVATAR_LEGGINGS,
    AVATAR_SHOES;

    override fun toString(): String {
        return when (this) {
            AVATAR_ACCESSORY -> "Accessory"
            AVATAR_BODY -> "Body"
            AVATAR_FACE -> "Face"
            AVATAR_HANDS -> "Hands"
            AVATAR_HEAD -> "Head"
            AVATAR_LEGGINGS -> "Leggings"
            AVATAR_SHOES -> "Shoes"
        }
    }
}