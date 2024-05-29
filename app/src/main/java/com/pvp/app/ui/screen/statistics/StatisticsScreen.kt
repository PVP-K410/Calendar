package com.pvp.app.ui.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.compose.common.shape.dashed
import com.patrykandpatrick.vico.core.cartesian.DefaultPointConnector
import com.patrykandpatrick.vico.core.cartesian.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.TopBottomShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.pvp.app.common.DateUtil.toLocalDate
import com.pvp.app.model.ActivityEntry
import com.pvp.app.ui.common.ProgressIndicatorWithinDialog
import com.pvp.app.ui.common.TabSelector
import com.pvp.app.ui.common.underline
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun StatisticsScreen(
    model: StatisticsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by model.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        ProgressIndicatorWithinDialog()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val labelOfSum = remember<(GraphType) -> String> {
            {
                when (it) {
                    is GraphType.Chain.Calories -> "kcal"
                    is GraphType.Chain.Steps -> "steps"
                    is GraphType.Chain.Distance -> "km"
                    else -> ""
                }
            }
        }

        var tab by remember { mutableIntStateOf(0) }

        val tabs = remember(state) {
            mapOf<String, @Composable () -> Unit>(
                "Ongoing" to {
                    GraphsOngoing(
                        labelOfSum = labelOfSum,
                        valuesWeek = state.valuesWeek,
                        valuesMonth = state.valuesMonth
                    )
                },
                "Past" to {
                    GraphsPast(
                        labelOfSum = labelOfSum,
                        values7d = state.values7d,
                        values30d = state.values30d
                    )
                }
            )
        }

        TabSelector(
            onSelect = { tab = it },
            tabs = tabs.keys.toList()
        )

        tabs.values.elementAt(tab)()

        Spacer(modifier = Modifier.size(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onSecondaryContainer,
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            StatisticItem(
                label = "Average tasks completed (7d):",
                value = "${state.averageTasksCompleted7d}"
            )
            StatisticItem(
                label = "Average tasks completed (30d):",
                value = "${state.averageTasksCompleted30d}"
            )
            StatisticItem(
                label = "Average points:",
                value = "${state.averagePoints}"
            )
            StatisticItem(
                label = "Top 3 frequent activities:",
                value = "${state.top3FrequentActivities}"
            )
            StatisticItem(
                label = "Unique activities (30d):",
                value = "${state.uniqueActivities30d}"
            )
        }
    }
}

@Composable
fun StatisticItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun Graph(
    valueFormatter: CartesianValueFormatter,
    values: List<Number>
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lines = listOf(
                    rememberLineSpec(
                        point = rememberShapeComponent(
                            color = colors[0],
                            margins = Dimensions.of(4.dp),
                            shape = Shape.Pill
                        ),
                        pointConnector = DefaultPointConnector(cubicStrength = 0.45f),
                        shader = TopBottomShader(
                            DynamicShader.color(colors[0]),
                            DynamicShader.color(colors[1])
                        )
                    )
                )
            ),
            bottomAxis = rememberBottomAxis(
                guideline = null,
                itemPlacer = remember { AxisItemPlacer.Horizontal.default() },
                label = TextComponent.build {
                    color = MaterialTheme.colorScheme.onSurface.toArgb()
                },
                valueFormatter = valueFormatter
            ),
            startAxis = rememberStartAxis(
                guideline = rememberLineComponent(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = remember {
                        Shape.dashed(
                            dashLength = 12.dp,
                            gapLength = 24.dp,
                            shape = Shape.Pill
                        )
                    }
                ),
                itemPlacer = remember { AxisItemPlacer.Vertical.count(count = { 5 }) },
                label = rememberAxisLabelComponent(
                    background = rememberShapeComponent(
                        color = Color.Transparent,
                        shape = Shape.Pill,
                        strokeColor = MaterialTheme.colorScheme.outlineVariant,
                        strokeWidth = 1.dp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    margins = Dimensions.of(end = 8.dp),
                    padding = Dimensions.of(
                        horizontal = 6.dp,
                        vertical = 2.dp
                    )
                )
            )
        ),
        marker = rememberDefaultCartesianMarker(
            label = TextComponent.build(),
            labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint
        ),
        model = CartesianChartModel(LineCartesianLayerModel.build { series(values) })
    )
}

@Composable
private fun GraphOfDays(
    labelAsDay: Boolean = true,
    labelOfSum: (GraphType) -> String,
    title: String,
    values: List<ActivityEntry>
) {
    val values = values.ifEmpty { listOf(ActivityEntry()) }
    var type by remember { mutableStateOf<GraphType.Chain>(GraphType.Chain.Steps) }

    val selector: (ActivityEntry) -> Int = remember(type) {
        when (type) {
            GraphType.Chain.Calories -> { entry -> (entry.calories / 1000).toInt() }
            GraphType.Chain.Steps -> { entry -> entry.steps.toInt() }
            GraphType.Chain.Distance -> { entry -> (entry.steps * 0.8 * 0.0001).toInt() }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)

            GraphTypeSelector(
                type = type,
                onTypeChange = { type = it }
            )
        }

        Graph(
            valueFormatter = { x, _, _ ->
                val date = values[x.toInt()].date.toLocalDate()

                if (labelAsDay) {
                    date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                } else {
                    date.dayOfMonth.toString()
                }
            },
            values = values.map { selector(it) }
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            text = "Total: ${values.sumOf { selector(it) }} ${labelOfSum(type)}",
            textAlign = TextAlign.End
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            text = "Average: ${values.sumOf { selector(it) } / values.size} ${labelOfSum(type)}",
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun GraphTypeSelector(
    type: GraphType.Chain,
    onTypeChange: (GraphType.Chain) -> Unit
) {
    Text(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .underline(
                color = MaterialTheme.colorScheme.outline,
                offset = 8.sp,
                width = 2.dp
            )
            .clickable { onTypeChange(GraphType.Chain.fromType(type.next)) }
            .padding(8.dp),
        text = type.title
    )
}

@Composable
private fun GraphsOngoing(
    labelOfSum: (GraphType) -> String,
    valuesWeek: List<ActivityEntry>,
    valuesMonth: List<ActivityEntry>
) {
    Spacer(modifier = Modifier.size(16.dp))

    GraphOfDays(
        labelOfSum = labelOfSum,
        title = "Current Week",
        values = valuesWeek
    )

    Spacer(modifier = Modifier.size(24.dp))

    GraphOfDays(
        labelAsDay = false,
        labelOfSum = labelOfSum,
        title = "Current Month",
        values = valuesMonth
    )
}

@Composable
private fun GraphsPast(
    labelOfSum: (GraphType) -> String,
    values7d: List<ActivityEntry>,
    values30d: List<ActivityEntry>
) {
    Spacer(modifier = Modifier.size(16.dp))

    GraphOfDays(
        labelOfSum = labelOfSum,
        title = "Past 7 Days",
        values = values7d
    )

    Spacer(modifier = Modifier.size(24.dp))

    GraphOfDays(
        labelAsDay = false,
        labelOfSum = labelOfSum,
        title = "Past 30 Days",
        values = values30d
    )
}

private sealed class GraphType(val title: String) {

    data object Calories : GraphType("Calories")

    data object Steps : GraphType("Steps")

    data object Distance : GraphType("Distance")

    sealed class Chain(
        current: GraphType,
        val next: GraphType
    ) : GraphType(current.title) {

        data object Calories : Chain(
            GraphType.Calories,
            GraphType.Steps
        )

        data object Steps : Chain(
            GraphType.Steps,
            GraphType.Distance
        )

        data object Distance : Chain(
            GraphType.Distance,
            GraphType.Calories
        )

        companion object {

            fun fromType(type: GraphType): Chain {
                return when (type) {
                    is GraphType.Calories -> Calories
                    is GraphType.Steps -> Steps
                    is GraphType.Distance -> Distance
                    else -> error("Unhandled graph type: $type")
                }
            }
        }
    }
}