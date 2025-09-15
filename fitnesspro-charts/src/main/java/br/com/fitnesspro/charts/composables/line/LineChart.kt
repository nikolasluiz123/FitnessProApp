package br.com.fitnesspro.charts.composables.line

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.fitnesspro.charts.composables.container.LineChartContainer
import br.com.fitnesspro.charts.states.line.LineChartState
import br.com.fitnesspro.charts.styles.line.LineChartStyle

@Composable
fun LineChart(
    state: LineChartState,
    style: LineChartStyle,
    modifier: Modifier = Modifier
) {
    require(style.lineStyles.isNotEmpty()) { "LineChartStyle requer pelo menos um LineStyle." }

    val maxValue = (state.entries.maxOfOrNull {
        it.values.maxOrNull() ?: 0f
    } ?: 0f).coerceAtLeast(1f)

    LineChartContainer(
        modifier = modifier,
        state = state,
        backgroundStyle = style.backgroundStyle,
        legendStyle = style.legendStyle,
        maxValue = maxValue
    ) { chartHeight, totalChartWidth, actualSlotWidth ->

        GroupLines(
            state = state,
            style = style,
            maxValue = maxValue,
            chartHeight = chartHeight,
            totalChartWidth = totalChartWidth,
            slotWidth = actualSlotWidth,
            isScrollable = style.backgroundStyle.enableHorizontalScroll
        )
    }
}