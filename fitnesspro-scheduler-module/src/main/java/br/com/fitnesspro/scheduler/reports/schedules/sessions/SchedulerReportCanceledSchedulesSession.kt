package br.com.fitnesspro.scheduler.reports.schedules.sessions

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.pdf.generator.common.IPageManager
import br.com.fitnesspro.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.scheduler.R as SchedulerRes

class SchedulerReportCanceledSchedulesSession(context: Context) : AbstractReportSession<SchedulerReportFilter>(context) {

    override suspend fun prepare(filter: SchedulerReportFilter) {
        super.prepare(filter)

        this.title = context.getString(SchedulerRes.string.scheduler_report_canceled_schedules_session_title)
    }

    override suspend fun draw(pageManager: IPageManager, yStart: Float): Float {
        val yStart = super.draw(pageManager, yStart)

        return yStart // TODO - Mudar isso depois
    }
}