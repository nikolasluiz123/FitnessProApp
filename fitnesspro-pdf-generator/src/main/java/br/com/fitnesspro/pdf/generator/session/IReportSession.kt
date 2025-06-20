package br.com.fitnesspro.pdf.generator.session

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.common.IPreparable

interface IReportSession<FILTER: Any>: IPreparable<FILTER> {

    fun shouldRender(filter: FILTER): Boolean = true

    fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Float): Float
}