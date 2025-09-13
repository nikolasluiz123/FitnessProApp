package br.com.fitnesspro.charts.composables.bar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            state.entries.forEachIndexed { index, entry ->
                val style = getValidStyle(state, index)

                ChartBar(
                    entry = entry,
                    style = style,
                    maxValue = maxValue,
                    chartHeight = chartHeight,
                    modifier = Modifier
                        .weight(1f, fill = true)
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