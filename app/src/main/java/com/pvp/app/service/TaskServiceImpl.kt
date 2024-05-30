@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pvp.app.service

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pvp.app.R
import com.pvp.app.api.Configuration
import com.pvp.app.api.ExerciseService
import com.pvp.app.api.ExperienceService
import com.pvp.app.api.MealService
import com.pvp.app.api.PointService
import com.pvp.app.api.TaskService
import com.pvp.app.api.UserService
import com.pvp.app.common.DateUtil.toNearestDate
import com.pvp.app.common.FlowUtil.firstOr
import com.pvp.app.common.JsonUtil.JSON
import com.pvp.app.common.JsonUtil.toJsonElement
import com.pvp.app.common.JsonUtil.toPrimitivesMap
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.GoogleTask
import com.pvp.app.model.Meal
import com.pvp.app.model.MealTask
import com.pvp.app.model.Points
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Locale
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

private val Context.dataStoreGoogleCalendarEvents: DataStore<Preferences> by preferencesDataStore(
    name = "google_calendar_events"
)

private val eventsKey: Preferences.Key<Set<String>> = stringSetPreferencesKey("events")

class TaskServiceImpl @Inject constructor(
    private val configuration: Configuration,
    @ApplicationContext private val context: Context,
    private val database: FirebaseFirestore,
    private val exerciseService: ExerciseService,
    private val experienceService: ExperienceService,
    private val mealService: MealService,
    private val pointService: PointService,
    private val userService: UserService
) : TaskService {

    override suspend fun claim(task: Task) {
        if (task.points.claimedAt != null) {
            error("Task points are already claimed")
        }

        val now = LocalDateTime.now()

        if (
            task.points.isExpired && (
                    task.date.year != now.year ||
                            task.date.dayOfYear < (now.dayOfYear -
                            configuration.limitPointsReclaimDays) ||
                            task.date.dayOfYear > now.dayOfYear
                    )
        ) {
            return
        }

        val tasksReclaimed = get(task.userEmail)
            .map {
                val date = task.date

                it
                    .filter { t -> t.date.isEqual(date) }
                    .filter { t -> t.points.claimedAt != null }
                    .filter { t -> t.points.isExpired }
            }
            .first().size

        if (tasksReclaimed >= configuration.limitPointsDeduction) {
            return
        }

        val taskNew = Task.copy(
            task,
            points = task.points.copy(claimedAt = now)
        )

        userService
            .get(taskNew.userEmail)
            .firstOrNull()
            ?.let { user ->
                val points = taskNew.points.value + (if (taskNew.points.isExpired) 1 else 0)
                val experience = user.experience + points

                userService.merge(
                    user.copy(
                        experience = experience,
                        level = experienceService.levelOf(experience),
                        points = user.points + points
                    )
                )
            }
            ?: error("User not found while claiming task points")

        update(
            taskNew,
            false
        )
    }

    override suspend fun create(
        date: LocalDate,
        description: String?,
        duration: Duration?,
        reminderTime: Duration?,
        time: LocalTime?,
        title: String,
        userEmail: String
    ): GeneralTask {
        val task = run {
            val task = GeneralTask(
                date = date,
                description = description,
                duration = duration,
                id = null,
                isCompleted = false,
                points = Points(),
                reminderTime = reminderTime,
                time = time?.cleanEnd(),
                title = title,
                userEmail = userEmail
            )

            GeneralTask.copy(
                task,
                points = task.points.copy(
                    isExpired = task.date.isBefore(LocalDate.now()),
                    value = pointService.calculate(task)
                )
            )
        }

        return merge(
            database,
            "General task creation failed",
            identifier,
            task
        )
    }

    override suspend fun create(
        activity: SportActivity,
        date: LocalDate,
        description: String?,
        distance: Double?,
        duration: Duration?,
        reminderTime: Duration?,
        isDaily: Boolean,
        time: LocalTime?,
        title: String,
        userEmail: String
    ): SportTask {
        val task = run {
            val task = SportTask(
                activity = activity,
                date = date,
                description = description,
                distance = distance,
                duration = duration,
                id = null,
                isCompleted = false,
                isDaily = isDaily,
                points = Points(),
                reminderTime = reminderTime,
                time = time?.cleanEnd(),
                title = title,
                userEmail = userEmail
            )

            SportTask.copy(
                task,
                points = task.points.copy(
                    isExpired = task.date.isBefore(LocalDate.now()),
                    value = pointService.calculate(
                        task = task,
                        increasePointYield = isWeekly(
                            activity,
                            userService
                        )
                    )
                )
            )
        }

        return merge(
            database,
            "Sport task creation failed",
            identifier,
            task
        )
    }

    override suspend fun create(
        date: LocalDate,
        duration: Duration?,
        reminderTime: Duration?,
        recipe: String,
        time: LocalTime?,
        title: String,
        userEmail: String
    ): CustomMealTask {
        val task = run {
            val task = CustomMealTask(
                date = date,
                duration = duration,
                id = null,
                isCompleted = false,
                points = Points(),
                recipe = recipe,
                reminderTime = reminderTime,
                time = time?.cleanEnd(),
                title = title,
                userEmail = userEmail
            )

            CustomMealTask.copy(
                task,
                points = task.points.copy(
                    isExpired = task.date.isBefore(LocalDate.now()),
                    value = pointService.calculate(task)
                )
            )
        }

        return merge(
            database,
            "Custom meal task creation failed",
            identifier,
            task
        )
    }

    override suspend fun create(
        date: LocalDate,
        duration: Duration?,
        mealId: String,
        reminderTime: Duration?,
        time: LocalTime?,
        title: String,
        userEmail: String
    ): MealTask {
        val task = run {
            val task = MealTask(
                date = date,
                duration = duration,
                id = null,
                isCompleted = false,
                mealId = mealId,
                points = Points(),
                reminderTime = reminderTime,
                time = time?.cleanEnd(),
                title = title,
                userEmail = userEmail
            )

            MealTask.copy(
                task,
                points = task.points.copy(
                    isExpired = task.date.isBefore(LocalDate.now()),
                    value = pointService.calculate(task)
                )
            )
        }

        return merge(
            database,
            "Meal task creation failed",
            identifier,
            task
        )
    }

    override suspend fun create(
        date: LocalDate,
        duration: Duration?,
        meal: Meal,
        reminderTime: Duration?,
        time: LocalTime?,
        title: String?,
        userEmail: String
    ): MealTask {
        val task = run {
            val task = MealTask(
                date = date,
                duration = duration,
                id = null,
                isCompleted = false,
                mealId = meal.id,
                points = Points(),
                reminderTime = reminderTime,
                time = time?.cleanEnd(),
                title = title ?: meal.name,
                userEmail = userEmail
            )

            MealTask.copy(
                task,
                points = task.points.copy(
                    isExpired = task.date.isBefore(LocalDate.now()),
                    value = pointService.calculate(task)
                )
            )
        }

        return merge(
            database,
            "Custom meal task creation failed",
            identifier,
            task
        )
    }

    override suspend fun generateDaily(
        count: Int,
        hasDisability: Boolean,
        userEmail: String
    ): List<SportTask> {
        return getDailySportActivities(
            count = count,
            hasDisability = hasDisability
        )
            .mapIndexed { index, activity ->
                val task = create(
                    activity = activity,
                    date = LocalDate.now(),
                    description = "One of your daily tasks for today",
                    distance = if (activity.supportsDistanceMetrics) {
                        getDistance(
                            activity = activity,
                            exerciseService = exerciseService
                        )
                    } else {
                        null
                    },
                    duration = if (!activity.supportsDistanceMetrics) {
                        getDuration(
                            activity = activity,
                            exerciseService = exerciseService
                        )
                    } else {
                        null
                    },
                    isDaily = true,
                    title = context.getString(
                        R.string.task_type_daily_label,
                        index + 1,
                        context.getString(activity.titleId)
                    ),
                    userEmail = userEmail
                )

                task
            }
    }

    override suspend fun generateMeal() {
        val dayToMeals = mealService.generateWeekPlan()

        dayToMeals
            .mapKeys { (day, _) -> day.toNearestDate() }
            .forEach { (day, meals) ->
                meals.forEachIndexed { index, meal ->
                    create(
                        date = day,
                        duration = Duration.ofMinutes(meal.readyInMinutes.toLong()),
                        meal = meal,
                        title = context.getString(
                            R.string.task_type_set_meal_label,
                            index + 1,
                            meal.name
                        ),
                        userEmail = userService.user.firstOrNull()?.email ?: error("User not found")
                    )
                }
            }
    }

    override suspend fun get(userEmail: String): Flow<List<Task>> {
        return database
            .collection(identifier)
            .whereEqualTo(
                Task::userEmail.name,
                userEmail
            )
            .snapshots()
            .map { qs ->
                qs.documents
                    .filter { it.exists() }
                    .mapNotNull { d ->
                        val task = d.data?.let { decodeByType(it) }

                        if (task is MealTask) {
                            return@mapNotNull MealTask.copy(
                                task,
                                mael = mealService
                                    .get(task.mealId)
                                    .first()
                            )
                        }

                        task
                    }
            }
            .combine(getGoogleEventTasks(context)) { tasks, tasksGoogle ->
                tasks.plus(tasksGoogle)
            }
    }

    override suspend fun remove(task: Task) {
        if (task.id == null) {
            error("Task id is required to remove it")
        }

        database
            .collection(identifier)
            .document(task.id!!)
            .delete()
            .await()
    }

    override suspend fun removeGoogle(task: GoogleTask) {
        editGoogleEventTasks(
            context,
            getGoogleEventTasks(
                context = context,
                emitEmpty = false
            )
                .firstOr(emptyList())
                .filter { it.id != task.id }
        )
    }

    override suspend fun removeAll(userEmail: String) {
        database
            .collection(identifier)
            .whereEqualTo(
                Task::userEmail.name,
                userEmail
            )
            .get()
            .await()
            .documents
            .forEach { d ->
                d.reference.delete()
            }
    }

    override suspend fun synchronizeGoogleCalendar(dateStart: LocalDate) {
        val user = userService.user.firstOrNull() ?: return

        val service = Calendar
            .Builder(
                NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                GoogleAccountCredential
                    .usingOAuth2(
                        context,
                        setOf(
                            CalendarScopes.CALENDAR_READONLY,
                            CalendarScopes.CALENDAR_EVENTS_READONLY
                        )
                    )
                    .also { it.selectedAccountName = user.email }
            )
            .setApplicationName("Calendar")
            .build()

        val now = DateTime(System.currentTimeMillis())

        val events = service
            .calendarList()
            .list()
            .execute().items
            .map { it.id }
            .flatMap { id ->
                service
                    .events()
                    .list(id)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute().items
                    .map {
                        val date = it.start.dateTime ?: it.start.date
                        val timeZone = it.start.timeZone

                        val timeZoneShift = if (timeZone != null && timeZone != "null") {
                            try {
                                ZonedDateTime
                                    .now()
                                    .withZoneSameInstant(ZoneId.of(timeZone)).offset.totalSeconds / 60
                            } catch (e: Exception) {
                                0
                            }
                        } else {
                            it.start.dateTime?.timeZoneShift ?: 0
                        }

                        val dateTime = LocalDateTime.ofEpochSecond(
                            date.value / 1000,
                            0,
                            ZoneOffset.ofTotalSeconds(timeZoneShift * 60)
                        )

                        GoogleTask(
                            date = dateTime.toLocalDate(),
                            description = it.description,
                            id = it.id,
                            time = dateTime.toLocalTime(),
                            title = it.summary
                        )
                    }
            }

        val eventsAll = getGoogleEventTasks(context)
            .firstOr(emptyList())
            .filter { it.date < dateStart }
            .plus(events)

        editGoogleEventTasks(
            context,
            eventsAll
        )
    }

    override suspend fun update(
        task: Task,
        updatePoints: Boolean
    ): Task {
        if (task.id == null) {
            error("Task id is required to update it.")
        }

        val timeNew = task.time?.cleanEnd()

        val points = if (updatePoints && task.points.claimedAt == null) {
            task.points.copy(
                value = pointService.calculate(task)
            )
        } else {
            task.points
        }

        val taskNew = Task.copy(
            task,
            points = points,
            time = timeNew
        )

        database
            .collection(identifier)
            .document(taskNew.id!!)
            .set(encodeByType(taskNew))
            .await()

        return taskNew
    }

    companion object {

        private fun LocalTime.cleanEnd(): LocalTime {
            return withSecond(0)
                .withNano(0)
        }

        private fun decodeByType(task: Map<String, Any>): Task {
            val element = task.toJsonElement()

            if (element !is JsonObject) {
                error("Task data is not a JSON object")
            }

            return when {
                element.containsKey(CustomMealTask::recipe.name) -> {
                    JSON.decodeFromJsonElement<CustomMealTask>(element)
                }

                element.containsKey(MealTask::mealId.name) -> {
                    JSON.decodeFromJsonElement<MealTask>(element)
                }

                element.containsKey(SportTask::activity.name) -> {
                    JSON.decodeFromJsonElement<SportTask>(element)
                }

                else -> {
                    JSON.decodeFromJsonElement<GeneralTask>(element)
                }
            }
        }

        private suspend fun editGoogleEventTasks(
            context: Context,
            tasks: List<GoogleTask>
        ) {
            context.dataStoreGoogleCalendarEvents.edit { preferences ->
                preferences[eventsKey] = tasks
                    .map { JSON.encodeToString<GoogleTask>(it) }
                    .toSet()
            }
        }

        private fun encodeByType(task: Task): Map<String, Any?> {
            return when (task) {
                is CustomMealTask -> JSON
                    .encodeToJsonElement<CustomMealTask>(task)
                    .toPrimitivesMap()

                is MealTask -> JSON
                    .encodeToJsonElement<MealTask>(task)
                    .toPrimitivesMap()

                is SportTask -> JSON
                    .encodeToJsonElement<SportTask>(task)
                    .toPrimitivesMap()

                else -> JSON
                    .encodeToJsonElement<GeneralTask>(task as GeneralTask)
                    .toPrimitivesMap()
            }
        }

        /**
         * @param baseDistance Represents a value (in km) that a user that walks 1km everyday
         * (activity level 1) would have as a maximum value assigned for a walking task
         */
        private suspend fun getDistance(
            activity: SportActivity,
            baseDistance: Double = 0.75,
            exerciseService: ExerciseService
        ): Double {
            val unit = baseDistance * 1000 / (1 / SportActivity.Walking.pointsRatioDistance)

            val multiplier = "%.2f"
                .format(
                    Locale.US,
                    exerciseService.calculateActivityLevel()
                )
                .toDouble()

            val upperBound = (unit * (1 / activity.pointsRatioDistance) * (multiplier) / 10)
                .roundToInt() * 10

            val lowerBound = if (multiplier < 2.0) {
                // For ensuring users don't get task to walk 50 meters
                upperBound / 2
            } else {
                (unit * (1 / activity.pointsRatioDistance) * (multiplier - 1) / 10).roundToInt() * 10
            }

            return (lowerBound..upperBound step 50)
                .toList()
                .random()
                .toDouble() / 1000
        }

        /**
         * @param baseDuration Represents a value that a user that walks 1km everyday (activity level 1)
         * would have as a maximum value assigned for playing basketball
         */
        private suspend fun getDuration(
            activity: SportActivity,
            baseDuration: Duration = Duration.ofMinutes(30),
            exerciseService: ExerciseService
        ): Duration {
            val unit = baseDuration.seconds / (1 / SportActivity.Basketball.pointsRatioDuration)

            val multiplier = "%.2f"
                .format(
                    Locale.US,
                    exerciseService.calculateActivityLevel()
                )
                .toDouble()

            // Division and multiplication by 300 are there to ensure upper and lower bounds
            // are multiples of 5 minutes
            val upperBound = ((unit * (1 / activity.pointsRatioDuration) * multiplier) / 300)
                .roundToInt() * 300

            val lowerBound = if (multiplier < 2.0) {
                max(
                    upperBound / 2,
                    300
                ) / 300 * 300
            } else {
                ((unit * (1 / activity.pointsRatioDuration) * (multiplier - 1)) / 300).roundToInt() * 300
            }

            return Duration.ofSeconds(
                (lowerBound..upperBound step 300)
                    .toList()
                    .random()
                    .toLong()
            )
        }

        private fun getDailySportActivities(
            count: Int,
            hasDisability: Boolean
        ): List<SportActivity> {
            return if (hasDisability) {
                listOf(SportActivity.Wheelchair)
            } else {
                SportActivity.entries
                    .minus(SportActivity.Wheelchair)
                    .minus(SportActivity.Other)
                    .minus(SportActivity.Walking)
                    .shuffled()
                    .take(count - 1)
                    .plus(SportActivity.Walking)
            }
        }

        private fun getGoogleEventTasks(
            context: Context,
            emitEmpty: Boolean = true
        ): Flow<List<GoogleTask>> {
            return context.dataStoreGoogleCalendarEvents.data
                .mapLatest { it[eventsKey] ?: emptySet() }
                .mapLatest { events ->
                    events.map {
                        JSON.decodeFromJsonElement<GoogleTask>(JSON.parseToJsonElement(it))
                    }
                }
                .onStart {
                    if (emitEmpty) {
                        emit(emptyList())
                    }
                }
        }

        private suspend fun isWeekly(
            activity: SportActivity,
            userService: UserService
        ): Boolean {
            return userService.user
                .firstOrNull()?.weeklyActivities
                ?.contains(activity)
                ?: false
        }

        @Suppress("UNCHECKED_CAST")
        private suspend fun <T : Task> merge(
            database: FirebaseFirestore,
            errorMessage: String,
            identifier: String,
            task: T
        ): T {
            val reference = database
                .collection(identifier)
                .add(encodeByType(task))
                .await()

            reference
                .update(
                    Task::id.name,
                    reference.id
                )
                .await()

            val snapshot = reference
                .get()
                .await()

            return snapshot.data
                ?.let {
                    when (task) {
                        is CustomMealTask -> decodeByType(it)
                        is GeneralTask -> decodeByType(it)
                        is MealTask -> decodeByType(it)
                        is SportTask -> decodeByType(it)
                        else -> decodeByType(it)
                    }
                } as? T
                ?: error(errorMessage)
        }
    }
}