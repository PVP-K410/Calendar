package com.pvp.app.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendObject(
    var friends: List<String> = emptyList(),
    var receivedRequests: List<String> = emptyList(),
    var sentRequests: List<String> = emptyList()
)