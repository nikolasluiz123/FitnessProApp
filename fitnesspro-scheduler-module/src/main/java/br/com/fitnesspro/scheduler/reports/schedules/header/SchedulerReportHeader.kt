package br.com.fitnesspro.scheduler.reports.schedules.header

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.pdf.generator.header.AbstractReportHeader
import br.com.fitnesspro.scheduler.R

class SchedulerReportHeader(context: Context): AbstractReportHeader<SchedulerReportFilter>(context) {

    override suspend fun prepare(filter: SchedulerReportFilter) {
        super.prepare(filter)

        this.title = context.getString(R.string.scheduler_report_header_title)
    }
}