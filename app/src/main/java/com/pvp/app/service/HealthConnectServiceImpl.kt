package com.pvp.app.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.pvp.app.api.HealthConnectService
import java.time.Instant
import javax.inject.Inject
import kotlin.reflect.KClass

class HealthConnectServiceImpl @Inject constructor(
    private val client: HealthConnectClient
) : HealthConnectService {

    override suspend fun aggregateActiveCalories(
        start: Instant,
        end: Instant
    ): Double {
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        start,
                        end
                    )
                )
            )

            response[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inCalories
            0.0
        } catch (e: Exception) {
            0.0
        }
    }

    override suspend fun aggregateDistance(
        start: Instant,
        end: Instant
    ): Double {
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        start,
                        end
                    )
                )
            )

            response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    override suspend fun aggregateSteps(
        start: Instant,
        end: Instant
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

    override suspend fun aggregateTotalCalories(
        start: Instant,
        end: Instant
    ): Double {
        return try {
            val response = client.aggregate(
                AggregateRequest(
                    metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(
                        start,
                        end
                    )
                )
            )

            response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inCalories
            0.0
        } catch (e: Exception) {
            0.0
        }
    }

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
}