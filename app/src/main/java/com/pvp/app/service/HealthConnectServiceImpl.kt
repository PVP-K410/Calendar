package com.pvp.app.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.pvp.app.api.HealthConnectService
import javax.inject.Inject
import kotlin.reflect.KClass

class HealthConnectServiceImpl @Inject constructor(
    private val healthConnectClient: HealthConnectClient
) : HealthConnectService {

    override suspend fun <T : Record> readActivityData(
        recordClass: KClass<T>,
        startTime: java.time.Instant,
        endTime: java.time.Instant
    ): List<T> {
        val request = ReadRecordsRequest(
            recordType = recordClass,
            timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        )
        val response = healthConnectClient.readRecords(request)
        return response.records
    }
}