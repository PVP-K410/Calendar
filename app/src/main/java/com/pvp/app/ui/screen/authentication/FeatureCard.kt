package com.pvp.app.ui.screen.authentication

import androidx.compose.ui.text.style.TextAlign

sealed class FeatureCard(
    val radius: Float = 0f,
    val text: String,
    val textAlign: TextAlign
) {

    data object ScheduleDay : FeatureCard(
        -10f,
        "Schedule your day",
        TextAlign.Start
    )

    data object AssignTasks : FeatureCard(
        15f,
        "Assign tasks",
        TextAlign.End
    )

    data object TrackCalories : FeatureCard(
        -10f,
        "Track your calories",
        TextAlign.Start
    )

    data object CompeteFriends : FeatureCard(
        15f,
        "Compete with friends",
        TextAlign.End
    )

    data object ReachGoals : FeatureCard(
        -15f,
        "Reach your goals",
        TextAlign.Start
    )

    data object AllInOne : FeatureCard(
        0f,
        "All in one application",
        TextAlign.End
    )

    companion object {

        val cards = listOf(
            ScheduleDay,
            AssignTasks,
            TrackCalories,
            CompeteFriends,
            ReachGoals,
            AllInOne
        )
    }
}