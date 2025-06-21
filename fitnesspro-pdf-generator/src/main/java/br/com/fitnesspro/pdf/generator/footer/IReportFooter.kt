package br.com.fitnesspro.pdf.generator.footer

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportFooter<FILTER: Any>: IPreparable<FILTER> {

    fun getHeight(pageInfo: PdfDocument.PageInfo): Float

    suspend fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo)
}