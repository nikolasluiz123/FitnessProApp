package br.com.fitnesspro.charts.composables.bar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.composables.ChartBackground
import br.com.fitnesspro.charts.states.bar.BarChartState
import br.com.fitnesspro.charts.styles.bar.BarStyle

@Composable
fun BarChart(
    state: BarChartState,
    modifier: Modifier = Modifier
) {
    ChartBackground(
        modifier = modifier,
        state = state
    ) { chartHeight, chartWidth ->
        val maxValue = (state.entries.maxOfOrNull { it.value } ?: 0f).coerceAtLeast(1f)
        val barWidthFraction = 0.5f
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
            state.entries.forEachIndexed { index, entry ->
                val barStyle = getValidStyle(state, index)
                val slotModifier = if (style.enableHorizontalScroll) {
                    Modifier.width(style.scrollableBarWidth)
                } else {
                    Modifier.weight(1f, fill = true)
                }

                ChartBar(
                    entry = entry,
                    style = barStyle,
                    maxValue = maxValue,
                    chartHeight = chartHeight,
                    index = index,
                    modifier = slotModifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(barWidthFraction)
                )
            }
        }
    }
}

private fun getValidStyle(state: BarChartState, index: Int): BarStyle {
    val styleFromList = state.barStyles.getOrNull(index)

    return if (styleFromList == null) {
        requireNotNull(state.defaultBarStyle)
        state.defaultBarStyle
    } else {
        styleFromList
    }
}