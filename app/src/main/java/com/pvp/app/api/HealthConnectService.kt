package com.pvp.app.api

import androidx.health.connect.client.records.Record
import java.time.Duration
import java.time.Instant
import kotlin.reflect.KClass

interface HealthConnectService {

    /**
     * Aggregates calories the user has burned through activities
     * between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the calorie count
     */
    suspend fun aggregateActiveCalories(
        start: Instant,
        end: Instant
    ): Double

    /**
     * Aggregates the distance user has traversed (by sport activities)
     * between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the distance in meters
     */
    suspend fun aggregateDistance(
        start: Instant,
        end: Instant
    ): Double

    /**
     * Aggregates the steps between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the step count (0 if the data could not be read)
     */
    suspend fun aggregateSteps(
        start: Instant,
        end: Instant
    ): Long

    /**
     * Aggregates sleep time between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns sleep duration (Duration.ZERO if no entries were found)
     */
    suspend fun aggregateSleepDuration(
        start: Instant,
        end: Instant
    ): Duration

    /**
     * Aggregates total count of calories the user has burned
     * between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the calorie count
     */
    suspend fun aggregateTotalCalories(
        start: Instant,
        end: Instant
    ): Double

    /**
     * Calculates and returns users average heart rate
     * between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the average heart rate
     */
    suspend fun getHeartRateAvg(
        start: Instant,
        end: Instant
    ): Long

    /**
     * Finds and returns users maximum heart rate
     * between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the maximum heart rate
     */
    suspend fun getHeartRateMax(
        start: Instant,
        end: Instant
    ): Long

    /**
     * Finds and returns users minimum heart rate
     * between the specified time range
     * @param start Specifies the start of time range
     * @param end Specifies the end of the time range
     * @return Returns the minimum heart rate
     */
    suspend fun getHeartRateMin(
        start: Instant,
        end: Instant
    ): Long

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
        start: Instant,
        end: Instant
    ): List<T>

    suspend fun permissionsGranted(
        permissions: Set<String>
    ): Boolean
}