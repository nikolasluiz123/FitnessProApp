package br.com.fitnesspro.pdf.generator.header

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import br.com.fitnesspro.pdf.generator.R
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.pdf.generator.utils.Position

abstract class AbstractReportHeader<FILTER : Any>(
    protected val context: Context
) : IReportHeader<FILTER> {

    protected lateinit var title: String
    protected lateinit var bitmap: Bitmap

    private val topPadding = Margins.MARGIN_32.toFloat()
    private val logoSize = 60f

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)
        this.bitmap = AppCompatResources.getDrawable(context, R.drawable.default_report_logo)?.toBitmap()!!
    }

    override suspend fun measureHeight(pageManager: IPageManager): Float {
        return topPadding + logoSize
    }

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        val pageWidth = pageManager.pageInfo.pageWidth.toFloat()
        val sidePadding = Margins.MARGIN_32.toFloat()

        val contentYStart = yStart + topPadding

        val (logoPosition, logoRect) = drawLogo(sidePadding, contentYStart, pageManager.canvas)
        val titlePosition = drawTitle(logoRect, logoPosition, pageManager.canvas)
        drawTitleLine(titlePosition, pageWidth, sidePadding, pageManager.canvas)

        return yStart + measureHeight(pageManager)
    }

    private fun drawLogo(padding: Float, contentYStart: Float, canvas: Canvas): Pair<Position, RectF> {
        val logoPosition = Position(padding, contentYStart)

        val logoRect = RectF(
            logoPosition.axisX,
            logoPosition.axisY,
            logoPosition.axisX + logoSize,
            logoPosition.axisY + logoSize
        )

        canvas.drawBitmap(bitmap, null, logoRect, null)

        return Pair(logoPosition, logoRect)
    }

    private fun drawTitle(logoRect: RectF, logoPosition: Position, canvas: Canvas): Position {
        val textStartX = logoRect.right + Margins.MARGIN_16
        val textBaselineY = logoPosition.axisY + (logoSize / 2) + (Paints.titlePaint.textSize / 3)

        val titlePosition = Position(textStartX, textBaselineY)

        canvas.drawText(title, titlePosition.axisX, titlePosition.axisY, Paints.titlePaint)

        return titlePosition
    }

    private fun drawTitleLine(titlePosition: Position, pageWidth: Float, padding: Float, canvas: Canvas) {
        val lineY = titlePosition.axisY + Margins.MARGIN_8

        canvas.drawLine(
            titlePosition.axisX,
            lineY,
            pageWidth - padding,
            lineY,
            Paints.titleLinePaint
        )
    }
}