package com.pvp.app.common

object InputValidator {
    fun validateBlank(input: String, fieldName: String): List<String> =
        if (input.isBlank()) listOf("$fieldName cannot be empty") else emptyList()

    fun validateLength(input: String, minLength: Int, fieldName: String): List<String> =
        if (input.length < minLength) listOf("$fieldName must be at least $minLength characters long") else emptyList()
}