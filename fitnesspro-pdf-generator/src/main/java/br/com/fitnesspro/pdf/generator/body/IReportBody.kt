package br.com.fitnesspro.pdf.generator.body

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.common.IPreparable
import br.com.fitnesspro.pdf.generator.session.IReportSession

interface IReportBody<FILTER: Any>: IPreparable<FILTER> {

    val sessions: MutableList<IReportSession<FILTER>>

    fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Int)

    override suspend fun prepare(filter: FILTER) {
        super.prepare(filter)

        sessions.forEach { it.prepare(filter) }
    }
}