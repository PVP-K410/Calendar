package com.pvp.app.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull

object FlowUtil {

    inline fun <reified T : Any?> List<Flow<T>>.flattenFlow(): Flow<List<T>> =
        combine(this@flattenFlow) {
            it.toList()
        }

    suspend fun <T : Any?> Flow<T>.firstOr(fallbackValue: T): T {
        return firstOrNull() ?: fallbackValue
    }
}