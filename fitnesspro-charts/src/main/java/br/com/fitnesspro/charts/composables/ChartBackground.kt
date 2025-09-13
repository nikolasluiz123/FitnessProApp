package br.com.fitnesspro.charts.composables

import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import br.com.fitnesspro.charts.states.bar.BarChartState
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.text.CanvasTextDrawer
import br.com.fitnesspro.charts.styles.text.extensions.getTextPaint

@Composable
fun ChartBackground(
    modifier: Modifier = Modifier,
    state: BarChartState,
    content: @Composable (chartHeight: Dp, chartWidth: Dp) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val chartHeight = this.maxHeight
        val chartWidth = this.maxWidth
        val maxValue = (state.entries.maxOfOrNull { it.value } ?: 0f).coerceAtLeast(1f)
        val style = state.backgroundStyle

        Canvas(modifier = Modifier.matchParentSize()) {
            val zeroY = size.height

            drawBaseLine(style, zeroY)
            drawElementsAxisY(style, maxValue, zeroY, chartHeight)

            if (style.showXAxisLabels) {
                val barWidth = size.width / (state.entries.size * 2)
                val xAxisLabelPaint = style.xAxisLabelStyle.getTextPaint(drawContext.density)

                state.entries.forEachIndexed { index, entry ->
                    val textDrawer = CanvasTextDrawer(
                        strategy = style.xAxisLabelStyle.longLabelStrategy,
                        textPaint = xAxisLabelPaint,
                        drawContext = drawContext
                    )

                    textDrawer.draw(
                        text = entry.label,
                        xCenter = (index * 2 + 1) * barWidth,
                        topY = size.height + style.xAxisLabelStyle.padding.toPx(),
                        availableWidth = barWidth
                    )
                }
            }
        }

        content(chartHeight, chartWidth)
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