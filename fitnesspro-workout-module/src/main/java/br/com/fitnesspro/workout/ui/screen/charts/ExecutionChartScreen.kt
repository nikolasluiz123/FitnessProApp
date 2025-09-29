package br.com.fitnesspro.workout.ui.screen.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.android.compose.charts.composables.bar.grouped.GroupedBarChart
import br.com.android.compose.charts.composables.line.LineChart
import br.com.android.compose.charts.styles.ChartContainerStyle
import br.com.android.compose.charts.styles.bar.BarStyle
import br.com.android.compose.charts.styles.bar.GroupedBarChartStyle
import br.com.android.compose.charts.styles.legend.ChartLegendStyle
import br.com.android.compose.charts.styles.line.LineChartStyle
import br.com.android.compose.charts.styles.line.LineStyle
import br.com.android.compose.charts.styles.text.ChartTextStyle
import br.com.android.compose.charts.styles.tooltip.ChartTooltipStyle
import br.com.android.ui.compose.components.topbar.SimpleTopAppBar
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.core.theme.BLUE_500
import br.com.fitnesspro.core.theme.GREEN_500
import br.com.fitnesspro.core.theme.ORANGE_500
import br.com.fitnesspro.core.theme.RED_500
import br.com.fitnesspro.workout.ui.screen.charts.enums.EnumChartType
import br.com.fitnesspro.workout.ui.state.ExecutionChartUIState
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionChartViewModel

@Composable
fun ExecutionGroupedBarChartScreen(
    viewModel: ExecutionChartViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExecutionGroupedBarChartScreen(
        state = state,
        onBackClick = onBackClick,
        onExecuteLoad = viewModel::loadStateWithDatabaseInfos
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExecutionGroupedBarChartScreen(
    state: ExecutionChartUIState,
    onBackClick: () -> Unit = {},
    onExecuteLoad: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = {
                            state.filterDialogState.onShowDialogChange(true)
                        }
                    ) {
                        Icon(
                            painter = painterResource(br.com.fitnesspro.core.R.drawable.ic_predefinitions_24dp),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LaunchedEffect(state.executeLoad) {
            if (state.executeLoad) {
                onExecuteLoad()
            }
        }

        FitnessProMessageDialog(state.messageDialogState)
        ExecutionGroupedBarChartFiltersDialog(state.filterDialogState)

        Column(
            Modifier
                .padding(paddingValues)
                .padding(top = 16.dp)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
        ) {
            val tooltipStyle = ChartTooltipStyle(
                textStyle = ChartTextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 10.sp,
                    padding = 0.dp
                ),
                backgroundColor = Color.Transparent,
                shadowElevation = 0.dp
            )

            val backgroundStyle = ChartContainerStyle(
                xAxisLabelStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                showYAxisLines = true,
                showYAxisLabels = false,
                enableHorizontalScroll = true,
                scrollableBarWidth = 128.dp
            )

            val colors = listOf(BLUE_500, GREEN_500, RED_500, ORANGE_500)

            val legendStyle = ChartLegendStyle(
                textStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                colors = colors
            )

            when (state.chartType) {
                EnumChartType.GROUPED_BAR -> {
                    val chartStyle = GroupedBarChartStyle(
                        defaultBarStyles = listOf(
                            BarStyle(fillColor = colors[0], tooltipStyle = tooltipStyle),
                            BarStyle(fillColor = colors[1], tooltipStyle = tooltipStyle),
                            BarStyle(fillColor = colors[2], tooltipStyle = tooltipStyle),
                            BarStyle(fillColor = colors[3], tooltipStyle = tooltipStyle),
                        ),
                        backgroundStyle = backgroundStyle,
                        legendStyle = legendStyle
                    )

                    GroupedBarChart(
                        state = state.barChartState,
                        style = chartStyle,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }

                EnumChartType.LINES -> {
                    val chartStyle = LineChartStyle(
                        lineStyles = listOf(
                            LineStyle(color = colors[0], tooltipStyle = tooltipStyle),
                            LineStyle(color = colors[1], tooltipStyle = tooltipStyle),
                            LineStyle(color = colors[2], tooltipStyle = tooltipStyle),
                            LineStyle(color = colors[3], tooltipStyle = tooltipStyle),
                        ),
                        backgroundStyle = backgroundStyle,
                        legendStyle = legendStyle
                    )

                    LineChart(
                        state = state.lineChartState,
                        style = chartStyle,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}