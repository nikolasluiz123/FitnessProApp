package br.com.fitnesspro.pdf.generator.components.table

import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.extensions.drawLineInPosition
import br.com.fitnesspro.pdf.generator.extensions.splitText
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.pdf.generator.utils.Position

class TableComponent<FILTER : Any>(
    private val columns: List<Column>,
    private val rows: List<List<String>>
) : IReportComponent<FILTER> {

    private val paddingHorizontal = Margins.MARGIN_32.toFloat()
    private val cellPadding = Margins.MARGIN_4.toFloat()

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        val pageWidth = pageManager.pageInfo.pageWidth.toFloat()
        val startX = paddingHorizontal
        val endX = pageWidth - paddingHorizontal
        val columnWidths = calculateColumnWidths(endX - startX)

        var currentY = yStart

        currentY = drawHeader(pageManager, startX, currentY, columnWidths)

        rows.forEach { row ->
            currentY = drawRow(pageManager, row, startX, currentY, columnWidths)
        }

        return currentY
    }

    private fun calculateColumnWidths(totalWidth: Float): List<Float> {
        val totalPercent = columns.sumOf { it.widthPercent.toDouble() }.toFloat()
        return columns.map { (it.widthPercent / totalPercent) * totalWidth }
    }

    private suspend fun drawHeader(
        pageManager: IPageManager,
        startX: Float,
        startY: Float,
        columnWidths: List<Float>
    ): Float {
        val headerHeight = estimateRowHeight(columns.map { it.label }, Paints.subtitlePaint, columnWidths)

        val y = pageManager.ensureSpace(startY, headerHeight)

        drawRowContent(pageManager.canvas, columns.map { it.label }, startX, y, columnWidths, Paints.subtitlePaint, isHeader = true)

        return y + headerHeight
    }

    private suspend fun drawRow(
        pageManager: IPageManager,
        row: List<String>,
        startX: Float,
        startY: Float,
        columnWidths: List<Float>
    ): Float {
        val rowHeight = estimateRowHeight(row, Paints.defaultValuePaint, columnWidths)

        val y = pageManager.ensureSpace(startY, rowHeight)

        drawRowContent(pageManager.canvas, row, startX, y, columnWidths, Paints.defaultValuePaint, isHeader = false)

        return y + rowHeight
    }

    private fun estimateRowHeight(
        texts: List<String>,
        paint: TextPaint,
        columnWidths: List<Float>
    ): Float {
        return texts.mapIndexed { index, text ->
            val lines = text.splitText(paint, columnWidths[index] - (cellPadding * 2))
            (lines.size * (paint.textSize + cellPadding)) + cellPadding
        }.maxOrNull() ?: 0f
    }

    private suspend fun drawRowContent(
        canvas: Canvas,
        texts: List<String>,
        startX: Float,
        startY: Float,
        columnWidths: List<Float>,
        paint: TextPaint,
        isHeader: Boolean
    ) {
        var currentX = startX
        var maxHeight = 0f

        texts.forEachIndexed { index, text ->
            val lines = text.splitText(paint, columnWidths[index] - (cellPadding * 2))
            val cellHeight = (lines.size * (paint.textSize + cellPadding)) + cellPadding
            maxHeight = maxOf(maxHeight, cellHeight)

            val align = if (isHeader) Paint.Align.LEFT else columns[index].alignment
            val baseY = startY + paint.textSize + cellPadding

            lines.forEachIndexed { lineIndex, line ->
                val textWidth = paint.measureText(line)
                val x = when (align) {
                    Paint.Align.LEFT -> currentX + cellPadding
                    Paint.Align.CENTER -> currentX + (columnWidths[index] / 2) - (textWidth / 2)
                    Paint.Align.RIGHT -> currentX + columnWidths[index] - cellPadding - textWidth
                }

                canvas.drawText(line, x, baseY + (lineIndex * (paint.textSize + cellPadding)), paint)
            }

            currentX += columnWidths[index]
        }

        drawHorizontalLine(canvas, startX, startY + maxHeight, columnWidths)
    }

    private suspend fun drawHorizontalLine(
        canvas: Canvas,
        startX: Float,
        y: Float,
        columnWidths: List<Float>
    ) {
        val endX = startX + columnWidths.sum()
        canvas.drawLineInPosition(
            Position(startX, y),
            Position(endX, y),
            Paints.dashedLinePaint
        )
    }
}