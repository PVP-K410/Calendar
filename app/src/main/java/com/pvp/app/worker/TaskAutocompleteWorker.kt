package com.pvp.app.worker

import android.content.Context
import android.util.Log
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.getDurationString
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@HiltWorker
class TaskAutocompleteWorker @AssistedInject constructor(
    @Assisted
    context: Context,
    @Assisted
    workerParams: WorkerParameters,
    private val exerciseService: ExerciseService,
    private val healthConnectService: HealthConnectService,
    private val taskService: TaskService,
    private val userService: UserService
) : CoroutineWorker(
    context,
    workerParams
) {
    companion object {

        const val WORKER_NAME = "TaskAutocompleteWorker"
    }

    override suspend fun doWork(): Result {
        val tasks = getTasks().sortedBy { task -> task.scheduledAt }

        val exercises = getActivities().map { exercise ->
            exerciseService.getExerciseInfo(exercise)
        }

        val completedTasks = checkTaskCompletion(
            tasks,
            exercises
        )

        updateTasks(completedTasks)

        Log.e("AUTOCOMPLETE", "FINISHED")

        return Result.success()
    }

    fun checkTaskCompletion(
        tasks: List<Task>,
        exercises: List<ExerciseSessionInfo>
    ): List<SportTask> {
        return tasks.mapNotNull { task ->
            val activity = (task as SportTask).activity
            var duration = task.duration ?: Duration.ZERO
            var distance = task.distance ?: 0.0
            var num = 1
            Log.e("AUTOCOMPLETE", "${task.title} ${activity.title} $duration $distance")
            exercises.forEach { exercise ->
                Log.e("AUTOCOMPLETE", "${exercise.record.exerciseType}")
                when (distance > 0.0 || !duration.isZero) {
                    true -> {
                        if (exercise.record.exerciseType == activity.id) {
                            when (activity.supportsDistanceMetrics) {
                                true -> {
                                    if (distance < exercise.distance!!){
                                        exercise.distance = exercise.distance!! - distance
                                        distance = 0.0
                                    }else{
                                        distance -= exercise.distance ?: 0.0
                                        exercise.distance = 0.0
                                    }
                                    Log.e(
                                        "AUTOCOMPLETE",
                                        "$num task ${task.title} ${activity.title} distance: $distance"
                                    )
                                }

                                else -> {
                                    if(duration < exercise.duration){
                                        exercise.duration = exercise.duration?.minus(duration);
                                        duration = Duration.ZERO
                                    }else{
                                        duration -= exercise.duration
                                        exercise.duration = Duration.ZERO
                                    }

                                    Log.e(
                                        "AUTOCOMPLETE",
                                        "$num task ${task.title} ${activity.title} distance: ${getDurationString(duration)}"
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        task.isCompleted = true

                        return@mapNotNull task
                    }
                }
            }

            null
        }
    }

    suspend fun getActivities(): List<ExerciseSessionRecord> {
        return healthConnectService.readActivityData(
            record = ExerciseSessionRecord::class,
            start = LocalDate
                .now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant(),
            end = Instant.now()
        )
    }

    suspend fun getTasks(): List<Task> {
        return userService.user
            .firstOrNull()
            ?.let { user ->
                taskService
                    .get(userEmail = user.email)
                    .map { tasks ->
                        tasks.filter { task ->
                            task is SportTask &&
                                    task.scheduledAt
                                        .toLocalDate()
                                        .isEqual(LocalDate.now())
                        }
                    }
                    .first()
            } ?: emptyList()
    }

    suspend fun updateTasks(tasks: List<Task>){
        tasks.forEach { task ->
            taskService.update(task)
        }
    }
}