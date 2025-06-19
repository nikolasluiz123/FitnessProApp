package br.com.fitnesspro.scheduler.reports

import android.content.Context
import br.com.fitnesspro.pdf.generator.footer.DefaultReportFooter
import br.com.fitnesspro.pdf.generator.report.AbstractPDFReport

class SchedulerPDFReport(
    private val context: Context,
    filter: SchedulerFilter
): AbstractPDFReport<SchedulerFilter>(filter) {

    override fun initialize() {
        this.header = SchedulerReportHeader(context)
        this.body = SchedulerReportBody(mutableListOf())
        this.footer = DefaultReportFooter()
    }
}