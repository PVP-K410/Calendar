package com.pvp.app.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dagger.hilt.EntryPoints

class Widget : GlanceAppWidget() {
    
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            GlanceTheme {
                WidgetContent()
            }
        }
    }

    @Composable
    private fun WidgetContent() {
        val appContext = LocalContext.current.applicationContext
        val model = EntryPoints
            .get(
                appContext,
                WidgetEntryPoint::class.java,
            )
            .getViewModel()

        WidgetStatistics(model = model)
    }

    @Composable
    private fun WidgetStatistics(model: WidgetViewModel) {
        val caloriesState = produceState(initialValue = 0.0) {
            model
                .getCaloriesFlow()
                .collect {
                    value = it
                }
        }

        val heartRateState = produceState(initialValue = 0.0) {
            model
                .getHeartRateFlow()
                .collect {
                    value = it.toDouble()
                }
        }

        val stepsState = produceState(initialValue = 0.0) {
            model
                .getStepsFlow()
                .collect {
                    value = it.toDouble()
                }
        }

        val calories = caloriesState.value
        val heartRate = heartRateState.value
        val steps = stepsState.value

        Log.e(
            "WidgetContent",
            "Calories: $calories, Heart Rate: $heartRate, Steps: $steps"
        )

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatisticItem(
                label = "Calories: ",
                value = calories.toString()
            )

            StatisticItem(
                label = "Heart Rate: ",
                value = heartRate.toString()
            )

            StatisticItem(
                label = "Steps: ",
                value = steps.toString()
            )
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
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer),
                text = label
            )

            Text(
                style = TextStyle(color = GlanceTheme.colors.onPrimaryContainer),
                text = value
            )
        }
    }
}
