package br.com.fitnesspro.pdf.generator.components.layout

import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.extensions.drawTextInPosition
import br.com.fitnesspro.pdf.generator.extensions.splitText
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.pdf.generator.utils.Position

class LayoutGridComponent<FILTER : Any>(
    private val items: List<Pair<String, String?>>,
    private val columnCount: Int = 3
) : IReportComponent<FILTER> {

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        val config = calculateGridConfig(pageManager.pageInfo)

        var columnIndex = 0
        var rowStartY = yStart + config.paddingTop
        var maxRowHeight = 0f

        items.forEachIndexed { index, (label, value) ->
            val startX = config.horizontalPaddingStart + (columnIndex * (config.columnWidth + config.columnSpacing))

            val labelHeight = Paints.defaultLabelPaint.textSize + Margins.MARGIN_4
            val valueHeight = estimateTextHeight(value ?: "", Paints.defaultValuePaint, config.columnWidth)
            val totalCellHeight = labelHeight + valueHeight + Margins.MARGIN_8

            rowStartY = pageManager.ensureSpace(rowStartY, totalCellHeight)

            val labelPos = Position(startX, rowStartY)
            val valuePos = Position(startX, rowStartY + labelHeight)

            pageManager.canvas.drawTextInPosition(label, labelPos, Paints.defaultLabelPaint, config.columnWidth)
            pageManager.canvas.drawTextInPosition(value ?: "", valuePos, Paints.defaultValuePaint, config.columnWidth)

            maxRowHeight = maxOf(maxRowHeight, totalCellHeight)

            columnIndex++
            if (columnIndex == columnCount || index == items.lastIndex) {
                rowStartY += maxRowHeight
                columnIndex = 0
                maxRowHeight = 0f
            }
        }

        return rowStartY - config.paddingTop
    }

    private fun calculateGridConfig(pageInfo: PdfDocument.PageInfo): GridConfig {
        val pageWidth = pageInfo.pageWidth.toFloat()
        val padding = Margins.MARGIN_32.toFloat()
        val spacing = Margins.MARGIN_16.toFloat()

        val usableWidth = pageWidth - (padding * 2) - (spacing * (columnCount - 1))
        val columnWidth = usableWidth / columnCount

        return GridConfig(
            pageWidth = pageWidth,
            horizontalPaddingStart = padding,
            columnSpacing = spacing,
            columnWidth = columnWidth,
            paddingTop = Margins.MARGIN_8.toFloat()
        )
    }

    private fun estimateTextHeight(text: String, paint: TextPaint, columnWidth: Float): Float {
        val lines = text.splitText(paint, columnWidth)
        return lines.size * (paint.textSize + Margins.MARGIN_4)
    }
}
