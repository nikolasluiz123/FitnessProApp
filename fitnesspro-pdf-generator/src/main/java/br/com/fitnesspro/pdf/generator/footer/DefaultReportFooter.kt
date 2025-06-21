package br.com.fitnesspro.pdf.generator.footer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.pdf.generator.R
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import java.time.LocalDate
import java.time.ZoneId

class DefaultReportFooter<FILTER: Any>(private val context: Context): IReportFooter<FILTER> {

    private lateinit var date: LocalDate
    private lateinit var bitmap: Bitmap

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)

        this.bitmap = AppCompatResources.getDrawable(context, R.drawable.default_report_logo)?.toBitmap()!!
        this.date = dateNow(ZoneId.systemDefault())
    }

    override fun getHeight(pageInfo: PdfDocument.PageInfo): Float {
        return 60f
    }

    override suspend fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo) {
        val padding = Margins.MARGIN_32.toFloat()
        val footerY = pageInfo.pageHeight - padding

        val logoWidth = 40f
        val logoHeight = 40f
        val logoRect = RectF(
            padding,
            footerY - logoHeight,
            padding + logoWidth,
            footerY
        )

        canvas.drawBitmap(bitmap, null, logoRect, null)

        val text = "${pageInfo.pageNumber}"
        val paint = Paints.defaultValuePaint
        val textWidth = paint.measureText(text)

        val textX = pageInfo.pageWidth - textWidth - padding
        val textY = footerY - ((logoHeight - paint.textSize) / 2)

        canvas.drawText(text, textX, textY, paint)

        canvas.drawLine(
            padding,
            footerY - logoHeight - Margins.MARGIN_8,
            pageInfo.pageWidth - padding,
            footerY - logoHeight - Margins.MARGIN_8,
            Paints.titleLinePaint
        )
    }
}