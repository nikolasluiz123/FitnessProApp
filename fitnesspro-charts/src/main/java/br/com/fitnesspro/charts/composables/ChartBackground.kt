package br.com.fitnesspro.charts.composables

import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
import br.com.fitnesspro.charts.states.bar.IBarChartState
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.text.CanvasTextDrawer
import br.com.fitnesspro.charts.styles.text.extensions.getTextPaint

@Composable
fun ChartBackground(
    modifier: Modifier = Modifier,
    state: IBarChartState,
    maxValue: Float,
    content: @Composable (chartHeight: Dp, chartWidth: Dp) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val chartHeight = this.maxHeight
        val viewportWidth = this.maxWidth
        val style = state.backgroundStyle
        val scrollState = rememberScrollState()

        val totalChartWidthDp: Dp = if (style.enableHorizontalScroll && state.entries.isNotEmpty()) {
            (state.entries.size * style.scrollableBarWidth).coerceAtLeast(viewportWidth)
        } else {
            viewportWidth
        }

        val scrollModifier = if (style.enableHorizontalScroll) {
            Modifier.horizontalScroll(scrollState)
        } else {
            Modifier
        }

        Box(modifier = scrollModifier) {
            Box(
                modifier = Modifier
                    .width(totalChartWidthDp)
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val zeroY = size.height
                    val canvasWidth = size.width

                    drawBaseLine(style, zeroY)
                    drawElementsAxisY(style, maxValue, zeroY, chartHeight)

                    if (style.showXAxisLabels) {
                        val xAxisLabelPaint = style.xAxisLabelStyle.getTextPaint(drawContext.density)

                        val (barSlotWidthPx, availableTextWidthPx) = if (style.enableHorizontalScroll) {
                            val widthPx = style.scrollableBarWidth.toPx()
                            Pair(widthPx, widthPx)
                        } else {
                            val barWidthOriginal = if (state.entries.isNotEmpty()) canvasWidth / (state.entries.size * 2) else 0f
                            Pair(barWidthOriginal, barWidthOriginal)
                        }


                        state.entries.forEachIndexed { index, entry ->
                            val textDrawer = CanvasTextDrawer(
                                strategy = style.xAxisLabelStyle.longLabelStrategy,
                                textPaint = xAxisLabelPaint,
                                drawContext = drawContext
                            )

                            val xCenter = if (style.enableHorizontalScroll) {
                                (index + 0.5f) * barSlotWidthPx
                            } else {
                                (index * 2 + 1) * barSlotWidthPx
                            }

                            textDrawer.draw(
                                text = entry.label,
                                xCenter = xCenter,
                                topY = size.height + style.xAxisLabelStyle.padding.toPx(),
                                availableWidth = availableTextWidthPx
                            )
                        }
                    }
                }

                content(chartHeight, totalChartWidthDp)
            }
        }
    }
}

private fun DrawScope.drawElementsAxisY(style: ChartBackgroundStyle, maxValue: Float, zeroY: Float, chartHeight: Dp) {
    if (style.showYAxisLabels || style.showYAxisLines) {
        val step = maxValue / style.yAxisSteps
        val yAxisLabelPaint = style.yAxisLabelStyle.getTextPaint(drawContext.density)

        for (i in 0..style.yAxisSteps) {
            val y = zeroY - (i * step / maxValue) * chartHeight.toPx()
            drawYAxisLine(style, y)
            drawYAxisLabel(style, i, step, y, yAxisLabelPaint)
        }
    }
}

private fun DrawScope.drawBaseLine(style: ChartBackgroundStyle, zeroY: Float) {
    drawLine(
        color = style.gridLineColor,
        start = Offset(0f, zeroY),
        end = Offset(size.width, zeroY),
        strokeWidth = style.gridLineWidth.toPx()
    )
}

private fun DrawScope.drawYAxisLabel(style: ChartBackgroundStyle, i: Int, step: Float, y: Float, yAxisLabelPaint: TextPaint) {
    if (style.showYAxisLabels) {
        drawContext.canvas.nativeCanvas.drawText(
            (i * step).toInt().toString(),
            0f + style.yAxisLabelStyle.padding.toPx(),
            y - style.yAxisLabelStyle.padding.toPx(),
            yAxisLabelPaint
        )
    }
}

private fun DrawScope.drawYAxisLine(style: ChartBackgroundStyle, y: Float) {
    if (style.showYAxisLines) {
        drawLine(
            color = style.gridLineColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = style.gridLineWidth.toPx()
        )
    }
}