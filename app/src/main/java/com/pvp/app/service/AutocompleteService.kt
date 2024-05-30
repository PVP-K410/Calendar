package com.pvp.app.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.pvp.app.R
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.GoalService
import com.pvp.app.api.HealthConnectService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.FlowUtil.firstOr
import com.pvp.app.model.ExerciseSessionInfo
import com.pvp.app.model.Goal
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
class AutocompleteService : Service() {

    @Inject
    lateinit var exerciseService: ExerciseService

    @Inject
    lateinit var goalService: GoalService

    @Inject
    lateinit var healthConnectService: HealthConnectService

    @Inject
    lateinit var taskService: TaskService

    @Inject
    lateinit var userService: UserService

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
                                        exercise.duration = exercise.duration?.minus(duration)
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
                        return@mapNotNull SportTask.copy(
                            task,
                            isCompleted = true
                        )
                    }
                }
            }

            null
        }
    }

    private suspend fun checkGoalCompletion(
        goals: List<Goal>
    ): List<Goal> {
        val exercises = healthConnectService
            .readActivityData(
                record = ExerciseSessionRecord::class,
                start = LocalDate
                    .now()
                    .minusDays(30)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant(),
                end = Instant.now()
            )
            .map {
                exerciseService.getExerciseInfo(it)
            }

        return goals.map { goal ->
            if (goal.completed) return@map goal

            when (goal.steps) {
                true -> {
                    goal.progress = getSteps(goal).toDouble()
                }

                false -> {
                    goal.progress = getDistance(
                        exercises,
                        goal
                    )
                }
            }

            goal.completed = goal.progress >= goal.target

            return@map goal
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat
            .Builder(
                this,
                com.pvp.app.model.NotificationChannel.TaskAutocomplete.channelId
            )
            .setOngoing(false)
            .setContentText(applicationContext.getString(R.string.worker_autocomplete_notification_description))
            .setContentTitle(applicationContext.getString(R.string.worker_autocomplete_notification_title))
            .setSilent(true)
            .setSmallIcon(R.drawable.logo)
            .build()
    }

    private suspend fun getActivities(startDate: LocalDate): List<ExerciseSessionRecord> {
        return healthConnectService.readActivityData(
            record = ExerciseSessionRecord::class,
            start = startDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant(),
            end = Instant.now()
        )
    }

    private fun getDistance(
        exercises: List<ExerciseSessionInfo>,
        goal: Goal
    ): Double {
        val startInstant = goal.startDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()

        return exercises.sumOf { exercise ->
            if (
                exercise.record.startTime.isAfter(startInstant)
                && exercise.record.exerciseType == goal.activity.id
            ) {
                exercise.distance ?: 0.0
            } else {
                0.0
            }
        } / 1000
    }

    private suspend fun getGoals(): List<Goal> {
        val user = userService.user.firstOrNull() ?: return emptyList()

        return goalService
            .get(user.email)
            .firstOr(emptyList())
            .filter { it.endDate.isAfter(LocalDate.now()) }
    }

    private suspend fun getSteps(goal: Goal): Long {
        return healthConnectService.aggregateSteps(
            start = goal.startDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant(),
            end = goal.endDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
        )
    }

    private suspend fun getTasks(): List<SportTask> {
        val user = userService.user.firstOrNull() ?: return emptyList()

        return taskService
            .get(userEmail = user.email)
            .map { tasks ->
                tasks
                    .mapNotNull { it as? SportTask }
                    .filter { task ->
                        task.date.isEqual(LocalDate.now())
                    }
            }
            .first()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        val notification = createNotification()

        startForeground(
            notification.channelId.hashCode(),
            notification
        )

        CoroutineScope(Dispatchers.IO)
            .launch {
                if (healthConnectService.permissionsGranted(PERMISSIONS)) {
                    val tasks = getTasks()
                        .sortedBy { task -> task.time }

                    val goals = getGoals()

                    val exercises = getActivities(LocalDate.now())
                        .map { exercise ->
                            exerciseService.getExerciseInfo(exercise)
                        }

                    val completedTasks = checkTaskCompletion(
                        tasks,
                        exercises
                    )

                    val completedGoals = checkGoalCompletion(goals)

                    updateGoals(completedGoals)

                    updateTasks(completedTasks)
                }
            }

        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private suspend fun updateGoals(goals: List<Goal>) {
        goals.forEach { goal ->
            goalService.update(goal)

            if (goal.completed && goal.points.claimedAt != null) {
                goalService.claim(goal)
            }
        }
    }

    private suspend fun updateTasks(tasks: List<Task>) {
        tasks.forEach { task ->
            taskService.update(task)
        }
    }

    companion object {

        private val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(ExerciseSessionRecord::class)
        )
    }
}