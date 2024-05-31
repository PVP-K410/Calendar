package com.pvp.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontStyle
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.pvp.app.R
import dagger.hilt.EntryPoints

class WidgetScreen : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        provideContent {
            val appContext = LocalContext.current.applicationContext
            val model = EntryPoints
                .get(
                    appContext,
                    WidgetEntryPoint::class.java,
                )
                .getViewModel()
            val state by model.state.collectAsState()

            GlanceTheme {
                WidgetContent(state = state)
            }
        }
    }

    @Composable
    private fun WidgetContent(state: WidgetState) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = "${state.user.username} ${getString(R.string.route_statistics)}",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontSize = 20.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center
                    ),
                )

                Spacer(GlanceModifier.size(8.dp))

                StatisticItem(
                    label = "${getString(R.string.dashboard_calories)}: ",
                    value = state.calories.toInt().toString()
                )

                StatisticItem(
                    label = "${getString(R.string.dashboard_BPM)}: ",
                    value = state.heartRate.toInt().toString()
                )

                StatisticItem(
                    label = "${getString(R.string.dashboard_steps)}: ",
                    value = state.steps.toInt().toString()
                )

                StatisticItem(
                    label = "${getString(R.string.dashboard_friends)}: ",
                    value = state.friendCount.toString()
                )

                StatisticItem(
                    label = "${getString(R.string.dashboard_decorations)}: ",
                    value = state.decorationCount.toString()
                )

                StatisticItem(
                    label = "${getString(R.string.dashboard_goals)}: ",
                    value = state.goals.size.toString()
                )

                StatisticItem(
                    label = "${getString(R.string.dashboard_tasks)}: ",
                    value = state.tasks.size.toString()
                )


            }
        }
    }

    @Composable
    private fun StatisticItem(
        label: String,
        value: String
    ) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = GlanceModifier.padding(end = 8.dp),
                style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer),
                text = label
            )

            Spacer(GlanceModifier.defaultWeight())

            Text(
                style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer),
                text = value
            )
        }
    }

    @Composable
    private fun getString(resId: Int): String {
        return LocalContext.current.getString(resId)
    }
}
