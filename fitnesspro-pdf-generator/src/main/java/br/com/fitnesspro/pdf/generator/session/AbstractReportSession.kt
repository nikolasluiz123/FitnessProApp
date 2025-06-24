package br.com.fitnesspro.pdf.generator.session

import android.content.Context
import android.graphics.Canvas
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints

abstract class AbstractReportSession<FILTER : Any>(
    protected val context: Context
) : IReportSession<FILTER> {

    protected lateinit var title: String
    protected var components: List<IReportComponent<FILTER>> = emptyList()

    override suspend fun measureHeight(pageManager: IPageManager): Float {
        val titleHeight = Paints.subtitlePaint.textSize + Margins.MARGIN_8
        val lineHeight = Margins.MARGIN_8
        var totalHeight = Margins.MARGIN_12 + titleHeight + lineHeight

        for (component in components) {
            totalHeight += component.measureHeight(pageManager)
        }

        return totalHeight
    }

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        val pageWidth = pageManager.pageInfo.pageWidth.toFloat()
        val paddingStart = Margins.MARGIN_32.toFloat()
        val newYStart = yStart + Margins.MARGIN_12.toFloat()

        val titleHeight = Paints.subtitlePaint.textSize + Margins.MARGIN_8
        val lineHeight = Margins.MARGIN_8
        val headerHeight = titleHeight + lineHeight

        val currentY = pageManager.ensureSpace(newYStart, headerHeight)

        val titleY = drawTitle(paddingStart, currentY, pageManager.canvas)
        val lineY = drawLine(titleY, pageManager.canvas, paddingStart, pageWidth)

        return drawComponents(lineY, pageManager)
    }

    private suspend fun drawComponents(lineY: Float, pageManager: IPageManager): Float {
        var currentY = lineY

        components.forEach { component ->
            currentY = component.draw(pageManager, currentY)
        }

        return currentY
    }

    private fun drawTitle(paddingStart: Float, yStart: Float, canvas: Canvas): Float {
        val titleX = paddingStart
        val titleY = yStart + Paints.subtitlePaint.textSize + Margins.MARGIN_8

        canvas.drawText(
            title,
            titleX,
            titleY,
            Paints.subtitlePaint
        )
        return titleY
    }

    private fun drawLine(titleY: Float, canvas: Canvas, paddingStart: Float, pageWidth: Float): Float {
        val lineStartY = titleY + Margins.MARGIN_8

        canvas.drawLine(
            paddingStart,
            lineStartY,
            pageWidth - paddingStart,
            lineStartY,
            Paints.titleLinePaint
        )

        return lineStartY
    }
}