package br.com.fitnesspro.charts.composables

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.withSave
import br.com.fitnesspro.charts.states.bar.BarChartState
import br.com.fitnesspro.charts.styles.text.LongLabelStrategy

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
                val paint = TextPaint().apply {
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

                state.entries.forEachIndexed { index, entry ->
                    val xCenter = (index * 2 + 1) * barWidth
                    val availableWidth = barWidth // espaço total da barra
                    val textWidth = paint.measureText(entry.label)

                    when (style.xAxisLabelStyle.longLabelStrategy) {
                        LongLabelStrategy.MultiLine -> {
                            // largura disponível (px)
                            val availableWidthPx = availableWidth.toInt().coerceAtLeast(1)

                            // TextPaint para medir e desenhar
                            val textPaint = TextPaint().apply {
                                color = style.xAxisLabelStyle.color.toArgb()
                                textSize = style.xAxisLabelStyle.fontSize.toPx()
                                typeface = Typeface.defaultFromStyle(
                                    when (style.xAxisLabelStyle.fontWeight) {
                                        FontWeight.Bold -> Typeface.BOLD
                                        else -> Typeface.NORMAL
                                    }
                                )
                                isAntiAlias = true
                                textAlign = Paint.Align.LEFT // alinhamento interno; nós calculamos a posição X
                            }

                            // split por espaços (apenas isso)
                            val words = entry.label.trim().split(Regex("\\s+"))
                            val lines = mutableListOf<String>()
                            var currentLine = ""

                            for (word in words) {
                                val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"

                                // se o candidato couber, aceita; senão "fecha" a linha atual e começa nova com a palavra
                                if (textPaint.measureText(candidate) <= availableWidthPx) {
                                    currentLine = candidate
                                } else {
                                    if (currentLine.isNotEmpty()) {
                                        lines.add(currentLine)
                                    }
                                    // inicia nova linha com a palavra atual (se a palavra for maior que a largura,
                                    // ela ficará sozinha na linha — sem quebra por caracteres)
                                    currentLine = word
                                }
                            }
                            if (currentLine.isNotEmpty()) lines.add(currentLine)

                            // desenha as linhas centralizadas em xCenter, uma abaixo da outra
                            val fm = textPaint.fontMetrics
                            val lineHeight = textPaint.fontSpacing // espaçamento recomendado entre linhas
                            val topY = size.height + style.xAxisLabelStyle.padding.toPx()
                            val firstBaseline = topY - fm.ascent // converte top -> baseline para a primeira linha

                            for ((i, line) in lines.withIndex()) {
                                val lineWidth = textPaint.measureText(line)
                                val x = xCenter - lineWidth / 2f
                                val y = firstBaseline + i * lineHeight
                                drawContext.canvas.nativeCanvas.drawText(line, x, y, textPaint)
                            }
                        }

                        LongLabelStrategy.Diagonal -> {
                            val fm = paint.fontMetrics
                            val topY = size.height + style.xAxisLabelStyle.padding.toPx()
                            val firstBaseline = topY - fm.ascent // baseline correto

                            val textWidth = paint.measureText(entry.label)
                            val offset = textWidth / 1.5f * kotlin.math.sin(Math.toRadians(45.0)).toFloat()

                            drawContext.canvas.nativeCanvas.withSave {
                                // Rotaciona em torno da base da barra
                                rotate(45f, xCenter, size.height)

                                // Aplica deslocamento calculado para que o texto "desça"
                                drawText(
                                    entry.label,
                                    xCenter + offset,
                                    firstBaseline,
                                    paint
                                )
                            }
                        }

                        LongLabelStrategy.Abbreviate -> {
                            val abbreviated = if (textWidth > availableWidth) {
                                val ellipsis = "..."
                                val maxChars = paint.breakText(
                                    entry.label, true, availableWidth, null
                                )
                                if (maxChars <= ellipsis.length) ellipsis
                                else entry.label.take(maxChars - ellipsis.length) + ellipsis
                            } else entry.label

                            val fm = paint.fontMetrics
                            val topY = size.height + style.xAxisLabelStyle.padding.toPx()
                            val firstBaseline = topY - fm.ascent // converte top -> baseline para a primeira linha

                            drawContext.canvas.nativeCanvas.drawText(
                                abbreviated,
                                xCenter,
                                firstBaseline,
                                paint
                            )
                        }
                    }
                }
            }
        }

        // barras, sobrepostas ao fundo
        content(chartHeight, chartWidth)
    }
}