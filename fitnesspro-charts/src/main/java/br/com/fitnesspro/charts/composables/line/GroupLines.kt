package br.com.fitnesspro.charts.composables.line

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import br.com.fitnesspro.charts.states.line.LineChartState
import br.com.fitnesspro.charts.styles.line.LineChartStyle

@Composable
internal fun GroupLines(
    state: LineChartState,
    style: LineChartStyle,
    maxValue: Float,
    chartHeight: Dp,
    totalChartWidth: Dp,
    slotWidth: Dp,
    isScrollable: Boolean
) {
    val density = LocalDensity.current
    val entries = state.entries
    if (entries.isEmpty()) return

    val chartHeightPx = with(density) { chartHeight.toPx() }
    val totalChartWidthPx = with(density) { totalChartWidth.toPx() }
    val slotWidthPx = with(density) { slotWidth.toPx() }

    val lineCount = style.lineStyles.size

    Box(modifier = Modifier.fillMaxSize()) {
        (0 until lineCount).forEach { lineIndex ->
            val lineStyle = style.lineStyles[lineIndex]

            val points = entries.mapIndexedNotNull { entryIndex, pointEntry ->
                val yValue = pointEntry.values.getOrNull(lineIndex)
                if (yValue == null) return@mapIndexedNotNull null

                val x = if (isScrollable) {
                    (entryIndex + 0.5f) * slotWidthPx
                } else {
                    val nonScrollableSlotWidth = totalChartWidthPx / entries.size
                    (entryIndex + 0.5f) * nonScrollableSlotWidth
                }

                val yFraction = (yValue / maxValue).coerceIn(0f, 1f)
                val y = chartHeightPx * (1f - yFraction)

                Offset(x, y)
            }

            Line(
                points = points,
                style = lineStyle,
                index = lineIndex
            )
        }
    }
}