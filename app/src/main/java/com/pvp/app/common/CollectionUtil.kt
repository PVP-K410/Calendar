package com.pvp.app.common

object CollectionUtil {

    fun <T> Collection<T>.indexOfOrNull(
        value: T
    ): Int? {
        val index = indexOf(value)

        return if (index == -1) null else index
    }
}