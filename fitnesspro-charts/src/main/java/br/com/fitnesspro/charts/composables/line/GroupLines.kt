package br.com.fitnesspro.charts.composables.line

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import br.com.fitnesspro.charts.composables.tooltip.ChartTooltip
import br.com.fitnesspro.charts.states.line.LineChartState
import br.com.fitnesspro.charts.styles.line.LineChartStyle
import kotlin.math.roundToInt

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

    val animationStates = remember { mutableStateMapOf<Int, Boolean>() }
    val allAnimationsDone by remember(animationStates.toMap()) {
        mutableStateOf(animationStates.size == lineCount && animationStates.values.all { it })
    }

    val allLinesPointsData = remember(entries, style, maxValue, chartHeightPx, totalChartWidthPx, slotWidthPx, isScrollable) {
        (0 until lineCount).map { lineIndex ->
            val lineStyle = style.lineStyles[lineIndex]
            val pointsData = entries.mapIndexedNotNull { entryIndex, pointEntry ->
                val yValue = pointEntry.values.getOrNull(lineIndex)
                if (yValue == null) return@mapIndexedNotNull null

                val x = if (isScrollable) {
                    (entryIndex + 0.5f) * slotWidthPx
                } else {
                    val nonScrollableSlotWidth = if (entries.isNotEmpty()) totalChartWidthPx / entries.size else 0f
                    (entryIndex + 0.5f) * nonScrollableSlotWidth
                }

                val yFraction = (yValue / maxValue).coerceIn(0f, 1f)
                val y = chartHeightPx * (1f - yFraction)

                Triple(Offset(x, y), yValue, lineStyle)
            }
            pointsData
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        allLinesPointsData.forEachIndexed { lineIndex, pointsData ->
            if (pointsData.isNotEmpty()) {
                Line(
                    points = pointsData.map { it.first },
                    style = pointsData.first().third,
                    index = lineIndex,
                    onAnimationEnd = {
                        animationStates[lineIndex] = true
                    }
                )
            } else {
                animationStates[lineIndex] = true
            }
        }

        if (allAnimationsDone) {
            allLinesPointsData.forEach { pointsData ->
                pointsData.forEach { (offset, value, lineStyle) ->
                    lineStyle.tooltipStyle?.let { tooltipStyle ->
                        ChartTooltip(
                            value = value,
                            style = tooltipStyle,
                            modifier = Modifier
                                .offset {
                                    IntOffset(offset.x.roundToInt(), offset.y.roundToInt())
                                }
                                .align(Alignment.TopStart)
                        )
                    }
                }
            }
        }
    }
}