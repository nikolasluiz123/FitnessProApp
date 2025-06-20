package br.com.fitnesspro.pdf.generator.footer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import java.time.LocalDate

class DefaultReportFooter<FILTER: Any>: IReportFooter<FILTER> {

    private lateinit var date: LocalDate
    private lateinit var bitmap: Bitmap

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)
    }

    override fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, totalPages: Int) {

    }
}