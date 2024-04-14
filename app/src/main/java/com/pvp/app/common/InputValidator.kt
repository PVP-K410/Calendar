package com.pvp.app.common

object InputValidator {

    /**
     * Validates if the input is blank. If it is, returns a list with an error message.
     */
    fun validateBlank(
        input: String,
        fieldName: String
    ): List<String> {
        return if (input.isBlank()) {
            listOf("$fieldName cannot be empty")
        } else {
            emptyList()
        }
    }

    /**
     * Validates if the input is of valid length. If it is not, returns a list with an error message.
     */
    fun validateLengthMin(
        input: String,
        minLength: Int,
        fieldName: String
    ): List<String> {
        return if (input.length < minLength) {
            listOf("$fieldName must be at least $minLength characters long")
        } else {
            emptyList()
        }
    }

    /**
     * Validates if the input is a valid float. If it is not, returns a list with an error message.
     */
    fun validateFloat(
        input: String,
        fieldName: String
    ): List<String> {
        return try {
            input.toFloat()

            emptyList()
        } catch (e: NumberFormatException) {
            listOf("$fieldName must be a valid number")
        }
    }
}