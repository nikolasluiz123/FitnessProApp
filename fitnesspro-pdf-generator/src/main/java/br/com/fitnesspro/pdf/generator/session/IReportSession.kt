package br.com.fitnesspro.pdf.generator.session

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.common.IPreparable
import br.com.fitnesspro.pdf.generator.components.IReportComponent

interface IReportSession<FILTER: Any>: IPreparable<FILTER> {
    val title: String
    val components: List<IReportComponent<FILTER>>

    fun shouldRender(filter: FILTER): Boolean

    fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Int)
}