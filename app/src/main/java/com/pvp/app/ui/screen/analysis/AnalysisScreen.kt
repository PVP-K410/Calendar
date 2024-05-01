@file:OptIn(ExperimentalMaterial3Api::class)

package com.pvp.app.ui.screen.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
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
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shader.TopBottomShader
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.pvp.app.common.DateUtil.toLocalDate
import com.pvp.app.model.ActivityEntry
import com.pvp.app.ui.common.ProgressIndicator
import com.pvp.app.ui.common.underline
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AnalysisScreen(
    model: AnalysisViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by model.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        ProgressIndicator()

        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        var tab by remember { mutableIntStateOf(0) }

        val tabs = remember(state) {
            mapOf<String, @Composable () -> Unit>(
                "On Going" to {
                    GraphsOnGoing(
                        valuesWeek = state.valuesWeek,
                        valuesMonth = state.valuesMonth
                    )
                },
                "Past" to {
                    GraphsPast(
                        values7d = state.values7d,
                        values30d = state.values30d
                    )
                }
            )
        }

        PrimaryTabRow(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
                .fillMaxWidth(0.9f)
                .clip(MaterialTheme.shapes.medium),
            selectedTabIndex = tab
        ) {
            tabs.onEachIndexed { index, (title, _) ->
                Tab(
                    modifier = Modifier.height(32.dp),
                    onClick = { tab = index },
                    selected = tab == index,
                ) { Text(title) }
            }
        }

        tabs.values.elementAt(tab)()
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
        model = CartesianChartModel(LineCartesianLayerModel.build { series(values) })
    )
}

@Composable
private fun GraphOfDays(
    labelAsDay: Boolean = true,
    title: String,
    values: List<ActivityEntry>
) {
    var type by remember { mutableStateOf<GraphType.Chain>(GraphType.Chain.Steps) }

    val selector = remember(type) {
        when (type) {
            GraphType.Chain.Calories -> { entry: ActivityEntry -> entry.calories / 1000 }
            GraphType.Chain.Steps -> ActivityEntry::steps
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
private fun GraphsOnGoing(
    valuesWeek: List<ActivityEntry>,
    valuesMonth: List<ActivityEntry>
) {
    GraphOfDays(
        title = "On Going Week",
        values = valuesWeek
    )

    GraphOfDays(
        labelAsDay = false,
        title = "On Going Month",
        values = valuesMonth
    )
}

@Composable
private fun GraphsPast(
    values7d: List<ActivityEntry>,
    values30d: List<ActivityEntry>
) {
    GraphOfDays(
        title = "Past 7 Days",
        values = values7d
    )

    GraphOfDays(
        labelAsDay = false,
        title = "Past 30 Days",
        values = values30d
    )
}

private sealed class GraphType(val title: String) {

    data object Calories : GraphType("Calories")

    data object Steps : GraphType("Steps")

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
            GraphType.Calories
        )

        companion object {

            fun fromType(type: GraphType): Chain {
                return when (type) {
                    is GraphType.Calories -> Calories
                    is GraphType.Steps -> Steps
                    else -> error("Unhandled graph type: $type")
                }
            }
        }
    }
}