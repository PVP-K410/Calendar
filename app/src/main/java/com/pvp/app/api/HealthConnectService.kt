package com.pvp.app.api

import androidx.health.connect.client.records.Record
import kotlin.reflect.KClass

interface HealthConnectService {
    /**
     * @param recordClass Specifies what kind of data needs to be read (steps, heart rate, etc.)
     * @param startTime Specifies from what time should the data be read
     * @param endTime Specifies until what time should the data be read
     * @return Returns a list of activities the user has done during the timeframe
     * and of the type specified
     */
    suspend fun <T : Record> readActivityData(
        recordClass: KClass<T>,
        startTime: java.time.Instant,
        endTime: java.time.Instant
    ): List<T>
}