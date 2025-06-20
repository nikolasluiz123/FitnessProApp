package br.com.fitnesspro.scheduler.reports

import android.content.Context
import br.com.fitnesspro.pdf.generator.header.AbstractReportHeader
import br.com.fitnesspro.scheduler.R as SchedulerRes

class SchedulerReportHeader(context: Context): AbstractReportHeader<SchedulerFilter>(context) {

    override suspend fun prepare(filter: SchedulerFilter) {
        super.prepare(filter)

        this.title = context.getString(SchedulerRes.string.scheduler_report_header_title)
    }
}