package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class Decoration(
    val description: String? = null,
    val id: String = "",
    val imageUrl: String = "",
    val name: String? = null,
    val price: Int
)