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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fitnesspro.charts.composables.bar.grouped.GroupedBarChart
import br.com.fitnesspro.charts.composables.line.LineChart
import br.com.fitnesspro.charts.styles.ChartContainerStyle
import br.com.fitnesspro.charts.styles.bar.BarStyle
import br.com.fitnesspro.charts.styles.bar.GroupedBarChartStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegendStyle
import br.com.fitnesspro.charts.styles.line.LineChartStyle
import br.com.fitnesspro.charts.styles.line.LineStyle
import br.com.fitnesspro.charts.styles.text.ChartTextStyle
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
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
            SimpleFitnessProTopAppBar(
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
            when (state.chartType) {
                EnumChartType.GROUPED_BAR -> {
                    val chartStyle = GroupedBarChartStyle(
                        defaultBarStyles = listOf(
                            BarStyle(fillColor = BLUE_500),
                            BarStyle(fillColor = GREEN_500),
                            BarStyle(fillColor = RED_500),
                            BarStyle(fillColor = ORANGE_500),
                        ),
                        backgroundStyle = ChartContainerStyle(
                            gridLineColor = MaterialTheme.colorScheme.outline,
                            xAxisLabelStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                            yAxisLabelStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                            enableHorizontalScroll = true,
                            yAxisSteps = 8,
                            scrollableBarWidth = 128.dp
                        ),
                        legendStyle = ChartLegendStyle(
                            textStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                            colors = listOf(BLUE_500, GREEN_500, RED_500, ORANGE_500)
                        )
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
                            LineStyle(color = BLUE_500),
                            LineStyle(color = GREEN_500),
                            LineStyle(color = RED_500),
                            LineStyle(color = ORANGE_500),
                        ),
                        backgroundStyle = ChartContainerStyle(
                            gridLineColor = MaterialTheme.colorScheme.outline,
                            xAxisLabelStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                            yAxisLabelStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                            enableHorizontalScroll = true,
                            yAxisSteps = 8,
                            scrollableBarWidth = 128.dp
                        ),
                        legendStyle = ChartLegendStyle(
                            textStyle = ChartTextStyle(color = MaterialTheme.colorScheme.onBackground),
                            colors = listOf(BLUE_500, GREEN_500, RED_500, ORANGE_500)
                        )
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