package br.com.fitnesspro.scheduler.reports.schedules.report

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.pdf.generator.footer.DefaultReportFooter
import br.com.fitnesspro.pdf.generator.report.AbstractPDFReport
import br.com.fitnesspro.scheduler.reports.schedules.body.SchedulerReportBody
import br.com.fitnesspro.scheduler.reports.schedules.header.SchedulerReportHeader
import br.com.fitnesspro.scheduler.reports.schedules.sessions.SchedulerReportCanceledSchedulesSession
import br.com.fitnesspro.scheduler.reports.schedules.sessions.SchedulerReportCompletedSchedulesSession
import br.com.fitnesspro.scheduler.reports.schedules.sessions.SchedulerReportConfirmedSchedulesSession
import br.com.fitnesspro.scheduler.reports.schedules.sessions.SchedulerReportPendingSchedulesSession
import br.com.fitnesspro.scheduler.reports.schedules.sessions.SchedulerReportResumeSession

class SchedulerPDFReport(
    private val context: Context,
    filter: SchedulerReportFilter
): AbstractPDFReport<SchedulerReportFilter>(filter) {

    override suspend fun initialize() {
        this.header = SchedulerReportHeader(context)

        this.body = SchedulerReportBody()
        this.body.addSession(SchedulerReportResumeSession(context))
        this.body.addSession(SchedulerReportPendingSchedulesSession(context))
        this.body.addSession(SchedulerReportConfirmedSchedulesSession(context))
        this.body.addSession(SchedulerReportCompletedSchedulesSession(context))
        this.body.addSession(SchedulerReportCanceledSchedulesSession(context))

        this.footer = DefaultReportFooter(context)
    }
}