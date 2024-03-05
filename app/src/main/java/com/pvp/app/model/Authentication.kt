package com.pvp.app.model

data class AuthenticationResult(
    val data: UserProperties? = null,
    val isSuccess: Boolean = false,
    val messageError: String? = null
)

data class AuthenticationState(
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