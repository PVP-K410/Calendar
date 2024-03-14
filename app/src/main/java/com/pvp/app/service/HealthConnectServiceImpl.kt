package com.pvp.app.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.pvp.app.api.HealthConnectService
import javax.inject.Inject
import kotlin.reflect.KClass

class HealthConnectServiceImpl @Inject constructor(
    private val client: HealthConnectClient
) : HealthConnectService {

    override suspend fun <T : Record> readActivityData(
        record: KClass<T>,
        start: java.time.Instant,
        end: java.time.Instant
    ): List<T> {
        val request = ReadRecordsRequest(
            recordType = record,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )

        return client.readRecords(request).records
    }

    override suspend fun aggregateSteps(
        start: java.time.Instant,
        end: java.time.Instant
    ): Long {
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(start, end)
                )
            )

            response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}