package br.com.fitnesspro.pdf.generator.components

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportComponent<FILTER: Any>: IPreparable<FILTER> {
    suspend fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Float): Float
}