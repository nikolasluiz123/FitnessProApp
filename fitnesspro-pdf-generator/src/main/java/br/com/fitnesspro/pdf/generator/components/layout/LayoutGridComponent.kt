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
        val pageWidth = pageInfo.pageWidth.toFloat()

        val horizontalPaddingStart = Margins.MARGIN_32.toFloat()
        val paddingTop = Margins.MARGIN_24.toFloat()
        val columnSpacing = Margins.MARGIN_16.toFloat()

        val usableWidth = (pageWidth - (horizontalPaddingStart * 2) - (columnSpacing * (columnCount - 1)))
        val columnWidth = usableWidth / columnCount

        var columnIndex = 0
        var rowStartY = yStart + paddingTop
        var maxRowHeight = 0f

        items.forEachIndexed { index, (label, value) ->
            val startX = horizontalPaddingStart + (columnIndex * (columnWidth + columnSpacing))
            val labelPosition = Position(startX, rowStartY)
            val valuePosition = Position(startX, rowStartY + Paints.defaultLabelPaint.textSize + Margins.MARGIN_4)

            // 🔸 Desenhar Label
            canvas.drawTextInPosition(label, labelPosition, Paints.defaultLabelPaint, columnWidth)

            // 🔸 Desenhar Value
            val valueHeight = canvas.drawTextInPosition(
                value ?: "",
                valuePosition,
                Paints.defaultValuePaint,
                columnWidth
            )

            // 🔸 Calcular maior altura da célula na linha
            val labelHeight = Paints.defaultLabelPaint.textSize + Margins.MARGIN_4
            val totalCellHeight = labelHeight + valueHeight + Margins.MARGIN_8

            if (totalCellHeight > maxRowHeight) {
                maxRowHeight = totalCellHeight
            }

            // 🔸 Controle de coluna
            columnIndex++

            if (columnIndex == columnCount || index == items.lastIndex) {
                // Finaliza linha
                rowStartY += maxRowHeight
                columnIndex = 0
                maxRowHeight = 0f
            }
        }

        // 🔧 Retorna a nova posição Y ajustada
        return rowStartY - paddingTop
    }
}