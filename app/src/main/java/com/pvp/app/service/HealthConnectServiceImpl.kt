package com.pvp.app.service

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.aggregate.AggregateMetric
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Energy
import androidx.health.connect.client.units.Length
import com.pvp.app.api.HealthConnectService
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import kotlin.reflect.KClass

class HealthConnectServiceImpl @Inject constructor(
    private val client: HealthConnectClient
) : HealthConnectService {

    private suspend fun aggregate(
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

    override suspend fun aggregateActiveCalories(
        start: Instant,
        end: Instant
    ): Double {
        return (aggregate(
            metric = ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL,
            start = start,
            end = end
        ) as? Energy)?.inCalories ?: 0.0
    }

    override suspend fun aggregateDistance(
        start: Instant,
        end: Instant
    ): Double {
        return (aggregate(
            metric = DistanceRecord.DISTANCE_TOTAL,
            start = start,
            end = end
        ) as? Length)?.inMeters ?: 0.0
    }

    override suspend fun aggregateSleepDuration(
        start: Instant,
        end: Instant
    ): Duration {
        return aggregate(
            metric = SleepSessionRecord.SLEEP_DURATION_TOTAL,
            start = start,
            end = end
        ) as? Duration ?: Duration.ZERO
    }

    override suspend fun aggregateSteps(
        start: Instant,
        end: Instant
    ): Long {
        return aggregate(
            metric = StepsRecord.COUNT_TOTAL,
            start = start,
            end = end
        ) as? Long ?: 0L
    }

    override suspend fun aggregateTotalCalories(
        start: Instant,
        end: Instant
    ): Double {
        return (aggregate(
            metric = TotalCaloriesBurnedRecord.ENERGY_TOTAL,
            start = start,
            end = end
        ) as? Energy)?.inCalories ?: 0.0
    }

    override suspend fun getHeartRateAvg(
        start: Instant,
        end: Instant
    ): Long {
        return aggregate(
            metric = HeartRateRecord.BPM_AVG,
            start = start,
            end = end
        ) as? Long ?: 0L
    }

    override suspend fun getHeartRateMax(
        start: Instant,
        end: Instant
    ): Long {
        return aggregate(
            metric = HeartRateRecord.BPM_MAX,
            start = start,
            end = end
        ) as? Long ?: 0L
    }

    override suspend fun getHeartRateMin(
        start: Instant,
        end: Instant
    ): Long {
        return aggregate(
            metric = HeartRateRecord.BPM_MIN,
            start = start,
            end = end
        ) as? Long ?: 0L
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

    override suspend fun permissionsGranted(permissions: Set<String>): Boolean {
        return client.permissionController
            .getGrantedPermissions()
            .containsAll(permissions)
    }
}