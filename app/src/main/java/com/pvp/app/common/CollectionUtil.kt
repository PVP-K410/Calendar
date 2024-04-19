package com.pvp.app.common

object CollectionUtil {

    /**
     * @return The index of the first occurrence of the specified element in the list, or null
     * if the list does not contain the element.
     */
    fun <T> Collection<T>.indexOfOrNull(value: T): Int? {
        val index = indexOf(value)

        return if (index == -1) null else index
    }

    /**
     * @return The first element of the list or the fallback value if the list is empty.
     */
    fun <T> List<T>.firstOr(fallbackValue: T): T {
        return if (isEmpty()) fallbackValue else first()
    }
}