package com.pvp.app.ui.screen.authentication

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.pvp.app.R

sealed class FeatureCard(
    val radius: Float = 0f,
    val text: @Composable () -> String,
    val textAlign: TextAlign
) {

    data object ScheduleDay : FeatureCard(
        -10f,
        { stringResource(R.string.authentication_featur_schedule_day) },
        TextAlign.Start
    )

    data object AssignTasks : FeatureCard(
        15f,
        { stringResource(R.string.authentication_featur_assign_tasks) },
        TextAlign.End
    )

    data object TrackCalories : FeatureCard(
        -10f,
        { stringResource(R.string.authentication_featur_track_calories) },
        TextAlign.Start
    )

    data object CompeteFriends : FeatureCard(
        15f,
        { stringResource(R.string.authentication_featur_compete_friends) },
        TextAlign.End
    )

    data object ReachGoals : FeatureCard(
        -15f,
        { stringResource(R.string.authentication_featur_reach_goals) },
        TextAlign.Start
    )

    data object AllInOne : FeatureCard(
        0f,
        { stringResource(R.string.authentication_featur_all_in_one) },
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