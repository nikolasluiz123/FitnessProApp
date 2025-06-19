package br.com.fitnesspro.scheduler.reports

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.body.IReportBody
import br.com.fitnesspro.pdf.generator.session.IReportSession

class SchedulerReportBody(
    override val sessions: MutableList<IReportSession<SchedulerFilter>>
) : IReportBody<SchedulerFilter> {

    override fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Int) {

    }
}