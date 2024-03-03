package com.pvp.app.model

data class AuthenticationResult(
    val data: UserProperties? = null,
    val messageError: String? = null
)

data class AuthenticationState(
    val isSuccessful: Boolean = false,
    val messageError: String? = null
)

data class UserProperties(
    val email: String,
    val id: String,
    val username: String
)