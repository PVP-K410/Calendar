package com.pvp.app.ui.screen.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.pvp.app.common.DateUtil.toLocalDate
import com.pvp.app.model.ActivityEntry
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AnalysisScreen(
    model: AnalysisViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by model.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        ProgressIndicatorWithinDialog()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AnalysisGraphOfDays(
            title = "Week Steps",
            values = state.valuesWeek
        )

        AnalysisGraphOfDays(
            labelAsDay = false,
            title = "Month Steps",
            values = state.valuesMonth
        )

        AnalysisGraphOfDays(
            title = "Last 7 Days Steps",
            values = state.values7d
        )

        AnalysisGraphOfDays(
            labelAsDay = false,
            title = "Last 30 Days Steps",
            values = state.values30d
        )
    }
}

@Composable
fun AnalysisGraphOfDays(
    labelAsDay: Boolean = true,
    selector: (ActivityEntry) -> Number = { it.steps },
    title: String,
    values: List<ActivityEntry>
) {
    val producer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(Unit) {
        producer.tryRunTransaction {
            lineSeries {
                if (values.isNotEmpty()) {
                    series(values.map { selector(it) })
                } else {
                    series(listOf(0))
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        Text(text = title)

        CartesianChartHost(
            rememberCartesianChart(
                rememberLineCartesianLayer(),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = { x, _, _ ->
                        values
                            .getOrNull(x.toInt())
                            ?.let {
                                if (labelAsDay) {
                                    it.date.toLocalDate().dayOfWeek.getDisplayName(
                                        TextStyle.SHORT,
                                        Locale.getDefault()
                                    )
                                } else {
                                    it.toString()
                                }
                            } ?: "No activity data available"
                    }
                )
            ),
            producer
        )
    }
}