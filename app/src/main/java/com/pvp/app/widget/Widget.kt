package com.pvp.app.widget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.hilt.navigation.compose.hiltViewModel

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
        val model: WidgetViewModel = hiltViewModel()
        val calories = model.getCalories()
        //var heartRate by remember { mutableLongStateOf(0) }
       // var steps by remember { mutableLongStateOf(0) }

        Log.e(
            "WidgetContent",
            "Calories: $calories, Heart Rate: $0, Steps: $0"
        )

        Column(
            modifier = GlanceModifier.fillMaxSize()
                .background(GlanceTheme.colors.background),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HELLLOO?",
                modifier = GlanceModifier.padding(12.dp)
            )


        }
    }
}
