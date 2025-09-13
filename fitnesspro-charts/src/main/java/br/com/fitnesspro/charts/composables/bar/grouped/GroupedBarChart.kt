package br.com.fitnesspro.charts.composables.bar.grouped

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.composables.container.BarChartContainer
import br.com.fitnesspro.charts.states.bar.GroupedBarChartState

@Composable
fun GroupedBarChart(
    state: GroupedBarChartState,
    modifier: Modifier = Modifier
) {
    require(state.defaultBarStyles.isNotEmpty()) { "GroupedBarChartState requer pelo menos um BarStyle em defaultBarStyles." }

    val maxValue = (state.entries.maxOfOrNull {
        it.values.maxOrNull() ?: 0f
    } ?: 0f).coerceAtLeast(1f)

    BarChartContainer(
        modifier = modifier,
        state = state,
        maxValue = maxValue
    ) { chartHeight, totalChartWidth, actualSlotWidth ->
        val barWidthFraction = 0.9f
        val style = state.backgroundStyle

        val rowArrangement = if (style.enableHorizontalScroll) {
            Arrangement.Start
        } else {
            Arrangement.SpaceEvenly
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = rowArrangement,
            verticalAlignment = Alignment.Bottom
        ) {
            state.entries.forEachIndexed { groupIndex, entry ->

                val slotModifier = if (style.enableHorizontalScroll) {
                    Modifier.width(actualSlotWidth)
                } else {
                    Modifier.weight(1f, fill = true)
                }

                GroupedBars(
                    entry = entry,
                    styles = state.defaultBarStyles,
                    maxValue = maxValue,
                    chartHeight = chartHeight,
                    groupIndex = groupIndex,
                    barWidthFraction = barWidthFraction,
                    modifier = slotModifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}