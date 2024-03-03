package com.pvp.app.common

object InputValidator {
    fun validateBlank(input: String, fieldName: String): List<String>{
        return if (input.isBlank()) {
             listOf("$fieldName cannot be empty")
        } else {
            emptyList()
        }
    }

    fun validateLength(input: String, minLength: Int, fieldName: String): List<String>{
        return if (input.length < minLength) {
            listOf("$fieldName must be at least $minLength characters long")
        } else {
            emptyList()
        }
    }

    fun validateFloat(input: String, fieldName: String) : List<String>{
        return try {
            input.toFloat()
            emptyList<String>()
        } catch (e: NumberFormatException) {
            listOf("$fieldName must be a valid number")
        }
    }
}