package com.pvp.app.common

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

object FlowUtil {

    inline fun <reified T : Any?> List<Flow<T>>.flattenFlow(): Flow<List<T>> =
        combine(this@flattenFlow) {
            it.toList()
        }
}