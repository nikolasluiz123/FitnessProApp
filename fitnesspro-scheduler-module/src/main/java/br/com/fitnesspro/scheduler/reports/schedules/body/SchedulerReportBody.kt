package br.com.fitnesspro.scheduler.reports.schedules.body

import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.pdf.generator.body.IReportBody
import br.com.fitnesspro.pdf.generator.session.IReportSession

class SchedulerReportBody(
    override val sessions: MutableList<IReportSession<SchedulerReportFilter>>,
    override val filter: SchedulerReportFilter,
) : IReportBody<SchedulerReportFilter> {

    override suspend fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Float) {
        var currentY = yStart

        sessions.forEach { session ->
            if (session.shouldRender(filter)) {
                currentY = session.draw(canvas, pageInfo, currentY)
            }
        }
    }
}