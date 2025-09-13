package br.com.fitnesspro.charts.styles.text

import android.text.TextPaint
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.nativeCanvas
import androidx.core.graphics.withSave
import br.com.fitnesspro.charts.styles.text.enums.LongLabelStrategy
import kotlin.math.sin

private const val ELLIPSES = "..."

class CanvasTextDrawer(
    private val strategy: LongLabelStrategy,
    private val textPaint: TextPaint,
    private val drawContext: DrawContext
) {
    fun draw(text: String, xCenter: Float, topY: Float, availableWidth: Float): Float {
        return when (strategy) {
            LongLabelStrategy.MultiLine -> {
                val lines = getLinesFromText(availableWidth, text)
                val lineHeight = textPaint.fontSpacing
                val firstBaseline = topY - textPaint.fontMetrics.ascent

                for ((i, line) in lines.withIndex()) {
                    val lineWidth = textPaint.measureText(line)
                    val x = xCenter - lineWidth / 2f
                    val y = firstBaseline + i * lineHeight

                    drawContext.canvas.nativeCanvas.drawText(line, x, y, textPaint)
                }

                lines.size * lineHeight
            }

            LongLabelStrategy.Diagonal -> {
                val firstBaseline = topY - textPaint.fontMetrics.ascent
                val textWidth = textPaint.measureText(text)
                val offset = textWidth / 1.5f * sin(Math.toRadians(45.0)).toFloat()

                drawContext.canvas.nativeCanvas.withSave {
                    rotate(45f, xCenter, drawContext.size.height)
                    drawText(text, xCenter + offset, firstBaseline, textPaint)
                }

                (textWidth * sin(Math.toRadians(45.0))).toFloat() + textPaint.fontSpacing
            }

            LongLabelStrategy.Abbreviate -> {
                val textWidth = textPaint.measureText(text)

                val abbreviated = if (textWidth > availableWidth) {
                    val maxChars = textPaint.breakText(
                        text,
                        true,
                        availableWidth,
                        null
                    )

                    if (maxChars <= ELLIPSES.length) {
                        ELLIPSES
                    } else {
                        text.take(maxChars - ELLIPSES.length) + ELLIPSES
                    }

                } else {
                    text
                }

                val firstBaseline = topY - textPaint.fontMetrics.ascent
                drawContext.canvas.nativeCanvas.drawText(abbreviated, xCenter, firstBaseline, textPaint)

                textPaint.fontSpacing
            }
        }
    }

    private fun getLinesFromText(availableWidth: Float, text: String): MutableList<String> {
        val availableWidthPx = availableWidth.toInt().coerceAtLeast(1)
        val words = splitTextBySpace(text)
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"

            if (textPaint.measureText(candidate) <= availableWidthPx) {
                currentLine = candidate
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                }

                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) lines.add(currentLine)

        return lines
    }

    private fun splitTextBySpace(text: String): List<String> {
        return text.trim().split(Regex("\\s+"))
    }
}