package com.pvp.app.service

import android.health.connect.datatypes.units.Energy
import android.health.connect.datatypes.units.Length
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.aggregate.AggregationResult
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
        return (aggregationResult(
            metric = ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
            start = start,
            end = end
        ) as? Energy)?.inCalories ?: 0.0
    }

    override suspend fun aggregateDistance(
        start: Instant,
        end: Instant
    ): Double {
        return (aggregationResult(
            metric = DistanceRecord.DISTANCE_TOTAL,
            start = start,
            end = end
        ) as? Length)?.inMeters ?: 0.0
    }

    override suspend fun aggregateSteps(
        start: Instant,
        end: Instant
    ): Long {
        return aggregationResult(
            metric = StepsRecord.COUNT_TOTAL,
            start = start,
            end = end
        ) as? Long ?: 0L
    }

    override suspend fun aggregateTotalCalories(
        start: Instant,
        end: Instant
    ): Double {
        return (aggregationResult(
            metric = TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            start = start,
            end = end
        ) as? Energy)?.inCalories ?: 0.0
    }

    private suspend fun aggregationResult(
        metric: AggregateMetric<*>,
        start: Instant,
        end: Instant
    ): Any? {
        return try {
            client.aggregate(
                AggregateRequest(
                    metrics = setOf(metric),
                    timeRangeFilter = TimeRangeFilter.between(
                        start,
                        end
                    )
                )
            )[metric]
        } catch (e: Exception) {
            null
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