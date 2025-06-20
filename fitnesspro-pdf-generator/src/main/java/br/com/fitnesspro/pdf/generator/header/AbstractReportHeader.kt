package br.com.fitnesspro.pdf.generator.header

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import br.com.fitnesspro.pdf.generator.R
import br.com.fitnesspro.pdf.generator.extensions.drawLineInPosition
import br.com.fitnesspro.pdf.generator.extensions.drawTextInPosition
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import br.com.fitnesspro.pdf.generator.utils.Position

abstract class AbstractReportHeader<FILTER : Any>(protected val context: Context) : IReportHeader<FILTER> {

    protected lateinit var title: String
    protected lateinit var bitmap: Bitmap

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)
        this.bitmap = AppCompatResources.getDrawable(context, R.drawable.default_report_logo)?.toBitmap()!!
    }

    override suspend fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, pageNumbers: Int) {
        val pageWidth = pageInfo.pageWidth
        val padding = Margins.MARGIN_30.toFloat()

        val (logoPosition, logoRect) = drawLogo(padding, canvas)
        val titlePosition = drawTitle(logoRect, logoPosition, canvas)
        drawTitleLine(titlePosition, pageWidth, padding, canvas)
    }

    private fun drawLogo(padding: Float, canvas: Canvas): Pair<Position, RectF> {
        val logoWidth = 60f
        val logoHeight = 60f
        val logoPosition = Position(padding, padding)

        val logoRect = RectF(
            logoPosition.axisX,
            logoPosition.axisY,
            logoPosition.axisX + logoWidth,
            logoPosition.axisY + logoHeight
        )

        canvas.drawBitmap(bitmap, null, logoRect, null)

        return Pair(logoPosition, logoRect)
    }

    private suspend fun drawTitle(logoRect: RectF, logoPosition: Position, canvas: Canvas): Position {
        val textStartX = logoRect.right + Margins.MARGIN_16
        val textStartY = logoPosition.axisY + Paints.titlePaint.textSize

        val titlePosition = Position(textStartX, textStartY + Margins.MARGIN_8)

        canvas.drawTextInPosition(
            text = title,
            position = titlePosition,
            paint = Paints.titlePaint
        )

        return titlePosition
    }

    private suspend fun drawTitleLine(titlePosition: Position, pageWidth: Int, padding: Float, canvas: Canvas) {
        val lineStart = Position(
            axisX = titlePosition.axisX,
            axisY = titlePosition.axisY + Margins.MARGIN_8
        )
        val lineEnd = Position(
            axisX = pageWidth - padding,
            axisY = lineStart.axisY
        )

        canvas.drawLineInPosition(
            startPosition = lineStart,
            endPosition = lineEnd,
            paint = Paints.titleLinePaint
        )
    }

}