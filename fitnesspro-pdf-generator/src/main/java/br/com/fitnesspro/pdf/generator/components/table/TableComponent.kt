package br.com.fitnesspro.pdf.generator.components.table

import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.withTranslation
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.components.table.enums.VerticalAlign
import br.com.fitnesspro.pdf.generator.components.table.layout.CellLayout
import br.com.fitnesspro.pdf.generator.components.table.layout.ColumnLayout
import br.com.fitnesspro.pdf.generator.components.table.layout.RowLayout
import br.com.fitnesspro.pdf.generator.components.table.layout.TableLayoutCache
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import kotlin.math.max

class TableComponent<FILTER : Any>(
    private val columnLayouts: List<ColumnLayout>,
    private val rows: List<List<String>>
) : IReportComponent<FILTER> {

    private val paddingHorizontal = Margins.MARGIN_32.toFloat()
    private val cellHorizontalPadding = Margins.MARGIN_4.toFloat()
    private val cellVerticalPadding = Margins.MARGIN_8.toFloat()

    private var tableLayoutCache: TableLayoutCache? = null

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        if (tableLayoutCache == null) {
            measureTable(pageManager)
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

    private fun measureTable(pageManager: IPageManager) {
        val totalWidth = pageManager.pageInfo.pageWidth.toFloat() - (paddingHorizontal * 2)
        val columnWidths = calculateColumnWidths(totalWidth)

        val headerTexts = columnLayouts.map { it.label }
        val headerLayout = createRowLayout(headerTexts, columnWidths, Paints.tableColumnLabelPaint, isHeader = true)

        val rowLayouts = rows.map { rowTexts ->
            createRowLayout(rowTexts, columnWidths, Paints.defaultValuePaint, isHeader = false)
        }

        tableLayoutCache = TableLayoutCache(header = headerLayout, rows = rowLayouts)
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

            val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, textWidth)
                .setAlignment(layoutAlign)
                .build()

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