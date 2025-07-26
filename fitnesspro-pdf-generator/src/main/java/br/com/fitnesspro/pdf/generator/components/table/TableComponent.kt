package br.com.fitnesspro.pdf.generator.components.table

import android.graphics.Paint
import android.text.Layout
import android.text.TextPaint
import androidx.core.graphics.withTranslation
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.components.table.enums.VerticalAlign
import br.com.fitnesspro.pdf.generator.components.table.layout.CellLayout
import br.com.fitnesspro.pdf.generator.components.table.layout.ColumnLayout
import br.com.fitnesspro.pdf.generator.components.table.layout.RowLayout
import br.com.fitnesspro.pdf.generator.components.table.layout.TableLayoutCache
import br.com.fitnesspro.pdf.generator.extensions.createStaticLayout
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import kotlin.math.max

/**
 * Implementação de um componente de Tabela. É muito comum termos um volume de dados grande para exibir
 * e costumeiramente escolher tabular esses dados, por isso foi implementado um componente que permite
 * organizar as colunas e realiza a divisão das linhas.
 *
 * @param columnLayouts Lista de configurações de colunas.
 * @param rows Lista de linhas.
 *
 * @author Nikolas Luiz Schmitt
 */
class TableComponent<FILTER : Any>(
    private val columnLayouts: List<ColumnLayout>,
    private val rows: List<List<String>>
) : IReportComponent<FILTER> {

    private val paddingHorizontal = Margins.MARGIN_32.toFloat()
    private val cellHorizontalPadding = Margins.MARGIN_4.toFloat()
    private val cellVerticalPadding = Margins.MARGIN_8.toFloat()

    /**
     * Armazena o layout da tabela que foi calculado em [measureHeight] para evitar recalcular
     * desnecessariamente. Como a tabela tem uma composição um pouco complexa foram utilizados alguns
     * objetos simples para representar partes dela.
     */
    private var tableLayoutCache: TableLayoutCache? = null

    /**
     * Armazena a altura do componente que foi calculada em [measureHeight] para evitar recalcular
     * desnecessariamente.
     */
    private var measuredTotalHeight: Float? = null

    override suspend fun measureHeight(pageManager: IPageManager): Float {
        measuredTotalHeight?.let { return it }

        val totalWidth = pageManager.pageInfo.pageWidth.toFloat() - (paddingHorizontal * 2)
        val columnWidths = calculateColumnWidths(totalWidth)

        val headerTexts = columnLayouts.map { it.label }
        val headerLayout = createRowLayout(headerTexts, columnWidths, Paints.tableColumnLabelPaint, isHeader = true)

        val rowLayouts = rows.map { rowTexts ->
            createRowLayout(rowTexts, columnWidths, Paints.defaultValuePaint, isHeader = false)
        }

        tableLayoutCache = TableLayoutCache(header = headerLayout, rows = rowLayouts)

        val totalHeight = headerLayout.height + rowLayouts.sumOf { it.height.toDouble() }.toFloat()
        measuredTotalHeight = totalHeight

        return totalHeight
    }

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        if (tableLayoutCache == null) {
            measureHeight(pageManager)
        }

        var currentY = yStart
        val startX = paddingHorizontal
        val columnWidths = calculateColumnWidths(pageManager.pageInfo.pageWidth.toFloat() - (paddingHorizontal * 2))

        tableLayoutCache?.header?.let { headerLayout ->
            currentY = pageManager.ensureSpace(currentY, headerLayout.height)
            drawRow(pageManager, headerLayout, startX, currentY, columnWidths)
            currentY += headerLayout.height
        }

        tableLayoutCache?.rows?.forEach { rowLayout ->
            currentY = pageManager.ensureSpace(currentY, rowLayout.height)
            drawRow(pageManager, rowLayout, startX, currentY, columnWidths)
            currentY += rowLayout.height
        }

        return currentY
    }

    private fun createRowLayout(texts: List<String>, columnWidths: List<Float>, paint: TextPaint, isHeader: Boolean): RowLayout {
        var maxRowHeight = 0f

        val cellLayouts = texts.mapIndexed { index, text ->
            val colWidth = columnWidths.getOrNull(index) ?: 0f
            val textWidth = (colWidth - (cellHorizontalPadding * 2)).toInt().coerceAtLeast(0)

            val horizontalAlignment = if (isHeader) Paint.Align.LEFT else columnLayouts.getOrNull(index)?.horizontalAlignment ?: Paint.Align.LEFT
            val layoutAlign = when (horizontalAlignment) {
                Paint.Align.CENTER -> Layout.Alignment.ALIGN_CENTER
                Paint.Align.RIGHT -> Layout.Alignment.ALIGN_OPPOSITE
                else -> Layout.Alignment.ALIGN_NORMAL
            }

            val staticLayout = text.createStaticLayout(
                paint = paint,
                width = textWidth,
                alignment = layoutAlign
            )

            maxRowHeight = max(maxRowHeight, staticLayout.height.toFloat())

            val verticalAlign = if (isHeader) VerticalAlign.CENTER else columnLayouts.getOrNull(index)?.verticalAlignment ?: VerticalAlign.CENTER
            CellLayout(staticLayout, verticalAlign)
        }

        val finalRowHeight = maxRowHeight + (cellVerticalPadding * 2)

        return RowLayout(cells = cellLayouts, height = finalRowHeight)
    }

    private fun drawRow(
        pageManager: IPageManager,
        rowLayout: RowLayout,
        startX: Float,
        startY: Float,
        columnWidths: List<Float>
    ) {
        val canvas = pageManager.canvas
        var currentX = startX

        rowLayout.cells.forEachIndexed { index, cellLayout ->
            val colWidth = columnWidths.getOrNull(index) ?: 0f

            if (colWidth > 0f) {
                val contentHeight = cellLayout.layout.height.toFloat()

                val yPos = when (cellLayout.verticalAlign) {
                    VerticalAlign.TOP -> startY + cellVerticalPadding
                    VerticalAlign.CENTER -> startY + (rowLayout.height - contentHeight) / 2
                    VerticalAlign.BOTTOM -> startY + rowLayout.height - contentHeight - cellVerticalPadding
                }

                canvas.withTranslation(currentX + cellHorizontalPadding, yPos) {
                    cellLayout.layout.draw(this)
                }
            }

            currentX += colWidth
        }

        val lineY = startY + rowLayout.height
        val endX = startX + columnWidths.sum()
        canvas.drawLine(startX, lineY, endX, lineY, Paints.dashedLinePaint)
    }

    private fun calculateColumnWidths(totalWidth: Float): List<Float> {
        val totalPercent = columnLayouts.sumOf { it.widthPercent.toDouble() }.toFloat()
        if (totalPercent == 0f) {
            return if (columnLayouts.isNotEmpty()) List(columnLayouts.size) { totalWidth / columnLayouts.size } else emptyList()
        }
        return columnLayouts.map { (it.widthPercent / totalPercent) * totalWidth }
    }
}