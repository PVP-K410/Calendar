package com.pvp.app.ui.screen.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.pvp.app.model.CustomMealTask
import com.pvp.app.model.GeneralTask
import com.pvp.app.model.GoogleTask
import com.pvp.app.model.MealTask
import com.pvp.app.model.SportActivity
import com.pvp.app.model.SportTask
import com.pvp.app.model.Task
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

open class TaskFormState<T : Task>(
    date: LocalDate,
    duration: Duration? = null,
    val editable: Boolean = true,
    isFormValid: Boolean = false,
    reminderTime: Duration? = null,
    task: T? = null,
    time: LocalTime? = null,
    title: String? = null
) {

    var date by mutableStateOf(date)
    var duration by mutableStateOf(duration)
    var isFormValid by mutableStateOf(isFormValid)
    var reminderTime by mutableStateOf(reminderTime)
    var task by mutableStateOf(task)
    var time by mutableStateOf(time)
    var title by mutableStateOf(title)

    constructor(state: TaskFormState<T>) : this(
        date = state.date,
        duration = state.duration,
        editable = state.editable,
        isFormValid = state.isFormValid,
        reminderTime = state.reminderTime,
        task = state.task,
        time = state.time,
        title = state.title
    )

    class CustomMeal(
        recipe: String = "",
        stateParent: TaskFormState<CustomMealTask>
    ) : TaskFormState<CustomMealTask>(stateParent) {

        var recipe by mutableStateOf(recipe)
    }

    class General(
        description: String? = null,
        stateParent: TaskFormState<GeneralTask>
    ) : TaskFormState<GeneralTask>(stateParent) {

        var description by mutableStateOf(description)
    }

    class Google(
        description: String? = null,
        stateParent: TaskFormState<GoogleTask>
    ) : TaskFormState<GoogleTask>(stateParent) {

        var description by mutableStateOf(description)
    }

    class Meal(
        meal: com.pvp.app.model.Meal? = null,
        stateParent: TaskFormState<MealTask>
    ) : TaskFormState<MealTask>(stateParent) {

        var meal by mutableStateOf(meal)
    }

    class Sport(
        activity: SportActivity = SportActivity.Walking,
        description: String? = null,
        distance: Double? = null,
        stateParent: TaskFormState<SportTask>
    ) : TaskFormState<SportTask>(stateParent) {

        var activity by mutableStateOf(activity)
        var description by mutableStateOf(description)
        var distance by mutableStateOf(distance)
    }

    companion object {

        fun <T : Task> create(
            date: LocalDate? = null,
            task: T? = null
        ) = TaskFormState(
            date = task?.date ?: date ?: LocalDate.now(),
            duration = task?.duration,
            editable = task !is GoogleTask && (task !is SportTask || !task.isDaily),
            isFormValid = task != null,
            reminderTime = task?.reminderTime,
            task = task,
            time = task?.time,
            title = task?.title
        )

        @Composable
        fun rememberCustomMealFormState(
            date: LocalDate? = null,
            task: CustomMealTask? = null
        ): CustomMeal {
            val state by remember {
                mutableStateOf(
                    CustomMeal(
                        recipe = task?.recipe ?: "",
                        stateParent = create(
                            date,
                            task
                        )
                    )
                )
            }

            return state
        }

        @Composable
        fun rememberGeneralFormState(
            date: LocalDate? = null,
            task: GeneralTask? = null
        ): General {
            val state by remember {
                mutableStateOf(
                    General(
                        description = task?.description,
                        stateParent = create(
                            date,
                            task
                        )
                    )
                )
            }

            return state
        }

        @Composable
        fun rememberGoogleFormState(
            date: LocalDate? = null,
            task: GoogleTask? = null
        ): Google {
            val state by remember {
                mutableStateOf(
                    Google(
                        description = task?.description,
                        stateParent = create(
                            date,
                            task
                        )
                    )
                )
            }

            return state
        }

        @Composable
        fun rememberMealFormState(
            date: LocalDate? = null,
            task: MealTask? = null
        ): Meal {
            val state by remember {
                mutableStateOf(
                    Meal(
                        meal = task?.meal,
                        stateParent = create(
                            date,
                            task
                        )
                    )
                )
            }

            return state
        }

        @Composable
        fun rememberSportFormState(
            date: LocalDate? = null,
            task: SportTask? = null
        ): Sport {
            val state by remember {
                mutableStateOf(
                    Sport(
                        activity = task?.activity ?: SportActivity.Walking,
                        description = task?.description,
                        distance = task?.distance,
                        stateParent = create(
                            date,
                            task
                        )
                    )
                )
            }

            return state
        }
    }
}