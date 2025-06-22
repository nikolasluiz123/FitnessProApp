package br.com.fitnesspro.pdf.generator.components.layout

import android.graphics.pdf.PdfDocument
import androidx.core.graphics.withSave
import androidx.core.graphics.withTranslation
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.extensions.createStaticLayout
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints

class LayoutGridComponent<FILTER : Any>(
    private val items: List<Pair<String, String?>>,
    private val columnCount: Int = 3
) : IReportComponent<FILTER> {

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        val config = calculateGridConfig(pageManager.pageInfo)
        val canvas = pageManager.canvas

        var columnIndex = 0
        var rowStartY = yStart + config.paddingTop
        var maxRowHeight = 0f

        items.forEachIndexed { index, (label, value) ->
            val startX = config.horizontalPaddingStart + (columnIndex * (config.columnWidth + config.columnSpacing))

            val labelLayout = label.createStaticLayout(
                paint = Paints.defaultLabelPaint,
                width = config.columnWidth.toInt()
            )

            val valueLayout = (value ?: "").createStaticLayout(
                paint = Paints.defaultValuePaint,
                width = config.columnWidth.toInt()
            )

            val labelHeight = labelLayout.height.toFloat()
            val valueHeight = valueLayout.height.toFloat()

            val totalCellHeight = labelHeight + valueHeight + Margins.MARGIN_16

            rowStartY = pageManager.ensureSpace(rowStartY, totalCellHeight)

            canvas.withTranslation(startX, rowStartY) {
                labelLayout.draw(this)
            }

            canvas.withSave {
                val valueY = rowStartY + labelHeight + Margins.MARGIN_4
                translate(startX, valueY)
                valueLayout.draw(this)
            }

            maxRowHeight = maxOf(maxRowHeight, totalCellHeight)

            columnIndex++
            if (columnIndex == columnCount || index == items.lastIndex) {
                rowStartY += maxRowHeight
                columnIndex = 0
                maxRowHeight = 0f
            }
        }

        return rowStartY - Margins.MARGIN_16
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
            paddingTop = Margins.MARGIN_16.toFloat()
        )
    }
}