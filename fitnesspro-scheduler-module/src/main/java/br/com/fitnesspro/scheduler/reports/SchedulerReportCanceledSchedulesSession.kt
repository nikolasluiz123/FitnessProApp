package br.com.fitnesspro.scheduler.reports

import android.content.Context
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import br.com.fitnesspro.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.scheduler.R as SchedulerRes

class SchedulerReportCanceledSchedulesSession(context: Context) : AbstractReportSession<SchedulerFilter>(context) {

    override suspend fun prepare(filter: SchedulerFilter) {
        super.prepare(filter)

        this.title = context.getString(SchedulerRes.string.scheduler_report_canceled_schedules_session_title)
    }

    override fun draw(canvas: Canvas, pageInfo: PdfDocument.PageInfo, yStart: Float): Float {
        val yStart = super.draw(canvas, pageInfo, yStart)

        return yStart // TODO - Mudar isso depois
    }
}