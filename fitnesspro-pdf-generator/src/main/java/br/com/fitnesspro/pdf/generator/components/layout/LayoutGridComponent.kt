package br.com.fitnesspro.pdf.generator.components.layout

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.extensions.drawTextInPosition
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.pdf.generator.utils.Position

class LayoutGridComponent<FILTER : Any>(
    private val items: List<Pair<String, String?>>,
    private val columnCount: Int = 3
) : IReportComponent<FILTER> {

    override suspend fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Float): Float {
        val config = calculateGridConfig(pageInfo)

        return drawGrid(canvas, config, yStart)
    }

    private fun calculateGridConfig(pageInfo: PdfDocument.PageInfo): GridConfig {
        val pageWidth = pageInfo.pageWidth.toFloat()
        val horizontalPaddingStart = Margins.MARGIN_32.toFloat()
        val columnSpacing = Margins.MARGIN_16.toFloat()
        val paddingTop = Margins.MARGIN_24.toFloat()

        val usableWidth = pageWidth - (horizontalPaddingStart * 2) - (columnSpacing * (columnCount - 1))
        val columnWidth = usableWidth / columnCount

        return GridConfig(
            pageWidth = pageWidth,
            horizontalPaddingStart = horizontalPaddingStart,
            columnSpacing = columnSpacing,
            columnWidth = columnWidth,
            paddingTop = paddingTop
        )
    }

    private suspend fun drawGrid(canvas: Canvas, config: GridConfig, yStart: Float): Float {
        var columnIndex = 0
        var rowStartY = yStart + config.paddingTop
        var maxRowHeight = 0f

        items.forEachIndexed { index, (label, value) ->
            val startX = getColumnStartX(config, columnIndex)
            val cellHeights = drawCell(canvas, label, value ?: "", startX, rowStartY, config.columnWidth)

            maxRowHeight = maxOf(maxRowHeight, cellHeights)

            columnIndex++

            if (isEndOfRow(columnIndex, index)) {
                rowStartY += maxRowHeight
                columnIndex = 0
                maxRowHeight = 0f
            }
        }

        return rowStartY - config.paddingTop
    }

    private fun getColumnStartX(config: GridConfig, columnIndex: Int): Float {
        return config.horizontalPaddingStart + (columnIndex * (config.columnWidth + config.columnSpacing))
    }

    private suspend fun drawCell(
        canvas: Canvas,
        label: String,
        value: String,
        startX: Float,
        startY: Float,
        columnWidth: Float
    ): Float {
        val labelPosition = Position(startX, startY)
        val valuePosition = Position(startX, startY + Paints.defaultLabelPaint.textSize + Margins.MARGIN_4)

        canvas.drawTextInPosition(label, labelPosition, Paints.defaultLabelPaint, columnWidth)
        val valueHeight = canvas.drawTextInPosition(value, valuePosition, Paints.defaultValuePaint, columnWidth)

        val labelHeight = Paints.defaultLabelPaint.textSize + Margins.MARGIN_4

        return labelHeight + valueHeight + Margins.MARGIN_8
    }

    private fun isEndOfRow(columnIndex: Int, itemIndex: Int): Boolean {
        return columnIndex == columnCount || itemIndex == items.lastIndex
    }
}