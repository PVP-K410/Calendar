package com.pvp.app.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf

object FlowUtil {

    /**
     * Flattens a list of flows into a single flow of list (**List<Flow<T>>** -> **Flow<List<T>>**).
     *
     * In case of an empty list, it straightforwardly returns flow of empty list.
     *
     * @return A flow of combined flows list.
     */
    inline fun <reified T : Any?> List<Flow<T>>.flattenFlow(): Flow<List<T>> =
        if (isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(this@flattenFlow) {
                it.toList()
            }
        }

    /**
     * @return The first value emitted by the flow or a fallback value if the flow is empty.
     */
    suspend fun <T : Any?> Flow<T>.firstOr(fallbackValue: T): T {
        return firstOrNull() ?: fallbackValue
    }
}