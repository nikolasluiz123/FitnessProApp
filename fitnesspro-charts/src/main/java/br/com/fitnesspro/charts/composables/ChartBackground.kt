package br.com.fitnesspro.charts.composables

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.states.bar.BarChartState

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
            // linha zero (eixo X)
            val zeroY = size.height
            drawLine(
                color = style.gridLineColor,
                start = Offset(0f, zeroY),
                end = Offset(size.width, zeroY),
                strokeWidth = style.gridLineWidth.toPx()
            )

            // grid horizontal + labels Y
            if (style.showYAxisLabels) {
                val step = maxValue / style.yAxisSteps
                for (i in 0..style.yAxisSteps) {
                    val y = zeroY - (i * step / maxValue) * chartHeight.toPx()

                    drawLine(
                        color = style.gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = style.gridLineWidth.toPx()
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        (i * step).toInt().toString(),
                        0f + style.yAxisLabelStyle.padding.toPx(),
                        y - style.yAxisLabelStyle.padding.toPx(),
                        Paint().apply {
                            color = style.yAxisLabelStyle.color.toArgb()
                            textSize = style.yAxisLabelStyle.fontSize.toPx()
                            textAlign = style.yAxisLabelStyle.textAlign
                            typeface = Typeface.defaultFromStyle(
                                when (style.yAxisLabelStyle.fontWeight) {
                                    FontWeight.Bold -> Typeface.BOLD
                                    else -> Typeface.NORMAL
                                }
                            )
                        }
                    )
                }
            }

            // labels X abaixo da linha 0
            if (style.showXAxisLabels) {
                val barWidth = size.width / (state.entries.size * 2)
                state.entries.forEachIndexed { index, entry ->
                    val xCenter = (index * 2 + 1) * barWidth

                    drawContext.canvas.nativeCanvas.drawText(
                        entry.label,
                        xCenter,
                        size.height + 16.dp.toPx(), // sempre na base
                        Paint().apply {
                            color = style.xAxisLabelStyle.color.toArgb()
                            textSize = style.xAxisLabelStyle.fontSize.toPx()
                            textAlign = style.xAxisLabelStyle.textAlign
                            typeface = Typeface.defaultFromStyle(
                                when (style.xAxisLabelStyle.fontWeight) {
                                    FontWeight.Bold -> Typeface.BOLD
                                    else -> Typeface.NORMAL
                                }
                            )
                        }
                    )
                }
            }
        }

        // barras, sobrepostas ao fundo
        content(chartHeight, chartWidth)
    }
}