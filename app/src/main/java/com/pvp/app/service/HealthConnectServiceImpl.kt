package com.pvp.app.service

import android.os.Build
import androidx.annotation.RequiresApi
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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun aggregateActiveCalories(
        start: Instant,
        end: Instant
    ): Double {
        return result(
            metric = ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
            start = start,
            end = end
        )?.let { aggregationResult ->
            aggregationResult[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inCalories ?: 0.0
        } ?: 0.0
    }

    override suspend fun aggregateDistance(
        start: Instant,
        end: Instant
    ): Double {
        return result(
            metric = DistanceRecord.DISTANCE_TOTAL,
            start = start,
            end = end
        )?.let { result ->
            result[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0.0
        } ?: 0.0
    }

    override suspend fun aggregateSteps(
        start: Instant,
        end: Instant
    ): Long {
        return result(
            metric = StepsRecord.COUNT_TOTAL,
            start = start,
            end = end
        )?.let { aggregationResult ->
            aggregationResult[StepsRecord.COUNT_TOTAL]
        } ?: 0L
    }

    override suspend fun aggregateTotalCalories(
        start: Instant,
        end: Instant
    ): Double {
        return result(
            metric = TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            start = start,
            end = end
        )?.let { aggregationResult ->
            aggregationResult[TotalCaloriesBurnedRecord.ENERGY_TOTAL]?.inCalories ?: 0.0
        } ?: 0.0
    }

    private suspend fun result(
        metric: AggregateMetric<*>,
        start: Instant,
        end: Instant
    ): AggregationResult? {
        return try {
            client.aggregate(
                AggregateRequest(
                    metrics = setOf(metric),
                    timeRangeFilter = TimeRangeFilter.between(
                        start,
                        end
                    )
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun <T : Record> readActivityData(
        record: KClass<T>,
        start: Instant,
        end: Instant
    ): List<T> {
        val request = ReadRecordsRequest(
            recordType = record,
            timeRangeFilter = TimeRangeFilter.between(start, end)
        )

        return client.readRecords(request).records
    }
}