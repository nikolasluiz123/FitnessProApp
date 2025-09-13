package br.com.fitnesspro.workout.ui.screen.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.composables.bar.grouped.GroupedBarChart
import br.com.fitnesspro.charts.entries.bar.GroupedBarEntry
import br.com.fitnesspro.charts.states.bar.GroupedBarChartState
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.bar.BarStyle
import br.com.fitnesspro.charts.styles.text.ChartTextStyle
import br.com.fitnesspro.charts.styles.text.enums.LongLabelStrategy
import br.com.fitnesspro.core.theme.BLUE_400
import br.com.fitnesspro.core.theme.GREEN_400
import br.com.fitnesspro.core.theme.ORANGE_400
import br.com.fitnesspro.core.theme.RED_400

@Composable
fun ExecutionBarChartScreen() {
    val state = GroupedBarChartState(
        entries = listOf(
            GroupedBarEntry("Janeiro", listOf(30f, 40f, 25f, 67f)),
            GroupedBarEntry("Fevereiro", listOf(20f, 55f, 45f, 97f)),
            GroupedBarEntry("Março", listOf(30f, 25f, 25f, 47f)),
            GroupedBarEntry("Abril", listOf(30f, 60f, 55f, 37f)),
        ),
        backgroundStyle = ChartBackgroundStyle(
            xAxisLabelStyle = ChartTextStyle(longLabelStrategy = LongLabelStrategy.Abbreviate),
            enableHorizontalScroll = true,
        ),
        defaultBarStyles = listOf(
            BarStyle(fillColor = BLUE_400),
            BarStyle(fillColor = GREEN_400),
            BarStyle(fillColor = RED_400),
            BarStyle(fillColor = ORANGE_400)
        )
    )

    GroupedBarChart(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}

//@Composable
//fun ExecutionBarChartScreen() {
//    val state = BarChartState(
//        entries = listOf(
//            SimpleBarEntry("Janeiro Muito Grande Mesmo", 30f),
//            SimpleBarEntry("Fevereiro", 55f),
//            SimpleBarEntry("Março", 80f),
//            SimpleBarEntry("Abril", 20f),
//            SimpleBarEntry("Abril", 20f),
//            SimpleBarEntry("Abril", 20f),
//            SimpleBarEntry("Abril", 20f),
//            SimpleBarEntry("Abril", 20f),
//            SimpleBarEntry("Abril", 20f)
//        ),
//        backgroundStyle = ChartBackgroundStyle(
//            xAxisLabelStyle = ChartTextStyle(longLabelStrategy = LongLabelStrategy.Abbreviate),
//            enableHorizontalScroll = true,
//        ),
//        defaultBarStyle = BarStyle(
//            fillColor = MaterialTheme.colorScheme.primary,
//        )
//    )
//
//    SimpleBarChart(
//        state = state,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp)
//            .padding(16.dp)
//    )
//}