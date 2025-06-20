package br.com.fitnesspro.pdf.generator.session

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.components.IReportComponent
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints

abstract class AbstractReportSession<FILTER: Any>(protected val context: Context): IReportSession<FILTER> {

    protected lateinit var title: String
    protected lateinit var components: List<IReportComponent<FILTER>>

    override fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Float): Float {
        val pageWidth = pageInfo.pageWidth.toFloat()
        val paddingStart = Margins.MARGIN_32.toFloat()

        val titleY = drawTitle(paddingStart, yStart, canvas)
        val lineY = drawLine(titleY, canvas, paddingStart, pageWidth)

        return lineY
    }

    private fun drawTitle(paddingStart: Float, yStart: Float, canvas: Canvas): Float {
        val titleX = paddingStart
        val titleY = yStart + Paints.subtitlePaint.textSize + Margins.MARGIN_48

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