package br.com.fitnesspro.scheduler.reports.schedules.report

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.pdf.generator.footer.DefaultReportFooter
import br.com.fitnesspro.pdf.generator.report.AbstractPDFReport
import br.com.fitnesspro.pdf.generator.session.IReportSession
import br.com.fitnesspro.scheduler.reports.schedules.body.SchedulerReportBody
import br.com.fitnesspro.scheduler.reports.schedules.header.SchedulerReportHeader
import br.com.fitnesspro.scheduler.reports.schedules.sessions.SchedulerReportPendingSchedulesSession
import br.com.fitnesspro.scheduler.reports.schedules.sessions.SchedulerReportResumeSession

class SchedulerPDFReport(
    private val context: Context,
    filter: SchedulerReportFilter
): AbstractPDFReport<SchedulerReportFilter>(filter) {

    override fun initialize() {
        this.header = SchedulerReportHeader(context)

        val sessions = mutableListOf<IReportSession<SchedulerReportFilter>>(
            SchedulerReportResumeSession(context),
            SchedulerReportPendingSchedulesSession(context),
        )

        this.body = SchedulerReportBody(sessions, filter)

        this.footer = DefaultReportFooter()
    }
}