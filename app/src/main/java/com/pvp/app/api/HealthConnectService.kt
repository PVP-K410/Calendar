package com.pvp.app.api

import androidx.health.connect.client.records.Record
import kotlin.reflect.KClass

interface HealthConnectService {

    /**
     * @param record Specifies what kind of data needs to be read (steps, heart rate, etc.)
     * @param start Specifies from what time should the data be read
     * @param end Specifies until what time should the data be read
     * @return Returns a list of activities the user has done during the timeframe
     * and of the type specified
     */
    suspend fun <T : Record> readActivityData(
        record: KClass<T>,
        start: java.time.Instant,
        end: java.time.Instant
    ): List<T>
}