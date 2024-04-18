package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendObject(
    var friends: List<Friends> = emptyList(),
    var receivedRequests: List<String> = emptyList(),
    var sentRequests: List<String> = emptyList()
)

@Serializable
data class Friends(
    val email: String = "",
    val since: Long = System.currentTimeMillis()
)