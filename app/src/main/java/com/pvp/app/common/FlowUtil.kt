package com.pvp.app.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

object FlowUtil {

    /**
     * Flattens a list of flows into a single flow of lists (**List<Flow<T>>** -> **Flow<List<T>>**).
     *
     * In case of an empty list, it may lock the thread, since it will never emit a value and the
     * implementation is based on [Flow.combine] principle, which requires at least one flow to emit
     * a value.
     *
     * @return A flow of combined flows lists.
     */
    inline fun <reified T : Any?> List<Flow<T>>.flattenFlow(): Flow<List<T>> =
        combine(this@flattenFlow) {
            it.toList()
        }

    /**
     * @return The first value emitted by the flow or a fallback value if the flow is empty.
     */
    suspend fun <T : Any?> Flow<T>.firstOr(fallbackValue: T): T {
        return firstOrNull() ?: fallbackValue
    }
}