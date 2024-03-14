package com.pvp.app.api

import androidx.health.connect.client.records.Record
import kotlin.reflect.KClass

interface HealthConnectService {

    /**
     * Reads all the data about a specified activity between the specified time range
     * @param record Specifies what kind of data needs to be read (steps, heart rate, etc.)
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns a list of specified activity occurrences the user has
     * participated in between the time range
     */
    suspend fun <T : Record> readActivityData(
        record: KClass<T>,
        start: java.time.Instant,
        end: java.time.Instant
    ): List<T>

    /**
     * Aggregates the steps between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the step count (0 if the data could not be read)
     */
    suspend fun aggregateSteps(
        start: java.time.Instant,
        end: java.time.Instant
    ): Long
}