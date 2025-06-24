package br.com.fitnesspro.pdf.generator.footer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.pdf.generator.R
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.utils.Margins
import br.com.fitnesspro.pdf.generator.utils.Paints
import java.time.LocalDateTime
import java.time.ZoneId

class DefaultReportFooter<FILTER : Any>(private val context: Context) : IReportFooter<FILTER> {

    private lateinit var date: LocalDateTime
    private lateinit var bitmap: Bitmap

    private val bottomPadding = Margins.MARGIN_32.toFloat()
    private val logoSize = 40f
    private val lineSpacing = Margins.MARGIN_8.toFloat()

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)
        this.bitmap = AppCompatResources.getDrawable(context, R.drawable.default_report_logo)?.toBitmap()!!
        this.date = LocalDateTime.now(ZoneId.systemDefault())
    }

    override suspend fun measureHeight(pageManager: IPageManager): Float {
        return lineSpacing + logoSize + bottomPadding
    }

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        val pageInfo = pageManager.pageInfo
        val canvas = pageManager.canvas
        val sidePadding = Margins.MARGIN_32.toFloat()

        val contentBottomY = pageInfo.pageHeight - bottomPadding

        val lineY = contentBottomY - logoSize - lineSpacing

        canvas.drawLine(
            sidePadding,
            lineY,
            pageInfo.pageWidth - sidePadding,
            lineY,
            Paints.titleLinePaint
        )

        val logoRect = RectF(
            sidePadding,
            contentBottomY - logoSize,
            sidePadding + logoSize,
            contentBottomY
        )

        canvas.drawBitmap(bitmap, null, logoRect, null)

        val textPaint = Paints.defaultValuePaint
        val formattedDateTime = date.format(EnumDateTimePatterns.DATE_TIME_SHORT)
        val dateText = context.getString(R.string.default_report_footer_generated_date_time, formattedDateTime)
        val pageNumberText = context.getString(R.string.default_report_footer_page_number, pageInfo.pageNumber)

        val dateTextY = contentBottomY - ((logoSize - textPaint.textSize) / 2)
        canvas.drawText(dateText, logoRect.right + Margins.MARGIN_8, dateTextY, textPaint)

        val pageTextWidth = textPaint.measureText(pageNumberText)
        val pageTextX = pageInfo.pageWidth - pageTextWidth - sidePadding
        val pageTextY = dateTextY
        canvas.drawText(pageNumberText, pageTextX, pageTextY, textPaint)

        return yStart
    }
}