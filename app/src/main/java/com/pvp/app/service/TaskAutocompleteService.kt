package com.pvp.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class TaskAutocompleteService : Service() {

    @Inject
    lateinit var exerciseService: ExerciseService

    @Inject
    lateinit var healthConnectService: HealthConnectService

    @Inject
    lateinit var taskService: TaskService

    @Inject
    lateinit var userService: UserService

    companion object {

        private const val NOTIFICATION_ID = Int.MAX_VALUE
    }

    private fun checkTaskCompletion(
        tasks: List<SportTask>,
        exercises: List<ExerciseSessionInfo>
    ): List<SportTask> {
        return tasks.mapNotNull { task ->
            var duration = task.duration ?: Duration.ZERO
            var distance = task.distance ?: 0.0

            exercises.forEach { exercise ->
                when (distance > 0.0 || !duration.isZero) {
                    true -> {
                        if (exercise.record.exerciseType == task.activity.id) {
                            when (task.activity.supportsDistanceMetrics) {
                                true -> {
                                    if (distance < exercise.distance!!) {
                                        exercise.distance = exercise.distance!! - distance
                                        distance = 0.0
                                    } else {
                                        distance -= exercise.distance ?: 0.0
                                        exercise.distance = 0.0
                                    }
                                }

                                false -> {
                                    if (duration < exercise.duration) {
                                        exercise.duration = exercise.duration?.minus(duration);
                                        duration = Duration.ZERO
                                    } else {
                                        duration -= exercise.duration
                                        exercise.duration = Duration.ZERO
                                    }
                                }
                            }
                        }
                    }

                    false -> {
                        task.isCompleted = true

                        return@mapNotNull task
                    }
                }
            }

            null
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(
            this,
            com.pvp.app.model.NotificationChannel.TaskAutocomplete.channelId
        )
            .setContentTitle("Task Processing")
            .setContentText("Processing your tasks in the background")
            .build()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            com.pvp.app.model.NotificationChannel.TaskAutocomplete.channelId,
            "Task Processing Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)
    }

    private suspend fun getActivities(): List<ExerciseSessionRecord> {
        return healthConnectService.readActivityData(
            record = ExerciseSessionRecord::class,
            start = LocalDate
                .now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant(),
            end = Instant.now()
        )
    }

    private suspend fun getTasks(): List<SportTask> {
        return userService.user
            .firstOrNull()
            ?.let { user ->
                taskService
                    .get(userEmail = user.email)
                    .map { tasks ->
                        tasks
                            .mapNotNull { it as? SportTask }
                            .filter { task ->
                                task.date.isEqual(LocalDate.now())
                            }
                    }
                    .first()
            } ?: emptyList()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
//        createNotificationChannel()
        val notification = createNotification()

        startForeground(
            NOTIFICATION_ID,
            notification
        )

        CoroutineScope(Dispatchers.IO).launch {
            val tasks = getTasks()
                .sortedBy { task -> task.time }

            val exercises = getActivities()
                .map { exercise ->
                    exerciseService.getExerciseInfo(exercise)
                }

            val completedTasks = checkTaskCompletion(
                tasks,
                exercises
            )

            updateTasks(completedTasks)
        }

        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun updateTasks(tasks: List<Task>) {
        tasks.forEach { task ->
            taskService.update(task)
        }
    }
}