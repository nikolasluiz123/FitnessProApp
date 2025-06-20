package br.com.fitnesspro.scheduler.reports

import android.content.Context
import br.com.fitnesspro.pdf.generator.footer.DefaultReportFooter
import br.com.fitnesspro.pdf.generator.report.AbstractPDFReport
import br.com.fitnesspro.pdf.generator.session.IReportSession

class SchedulerPDFReport(
    private val context: Context,
    filter: SchedulerFilter
): AbstractPDFReport<SchedulerFilter>(filter) {

    override fun initialize() {
        this.header = SchedulerReportHeader(context)

        val sessions = mutableListOf<IReportSession<SchedulerFilter>>(
            SchedulerReportResumeSession(context),
            SchedulerReportPendingSchedulesSession(context),
            SchedulerReportCanceledSchedulesSession(context),
            SchedulerReportCompletedSchedulesSession(context)
        )

        this.body = SchedulerReportBody(sessions)

        this.footer = DefaultReportFooter()
    }
}