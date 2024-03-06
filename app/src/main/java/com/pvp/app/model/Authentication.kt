package com.pvp.app.model

data class AuthenticationResult(
    val properties: UserProperties? = null,
    val isSuccess: Boolean = false,
    val messageError: String? = null
)

data class SignOutResult(
    val isSuccess: Boolean = false,
    val messageError: String? = null
)

data class UserProperties(
    val email: String,
    val id: String,
    val username: String
)