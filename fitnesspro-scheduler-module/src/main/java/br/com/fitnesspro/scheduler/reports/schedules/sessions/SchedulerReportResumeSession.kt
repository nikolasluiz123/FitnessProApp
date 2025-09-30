package br.com.fitnesspro.scheduler.reports.schedules.sessions

import android.content.Context
import br.com.android.pdf.generator.components.layout.LayoutGridComponent
import br.com.android.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.reports.injection.ISchedulerReportsEntryPoint
import br.com.fitnesspro.tuple.reports.schedulers.SchedulersResumeSessionReportTuple
import dagger.hilt.android.EntryPointAccessors

class SchedulerReportResumeSession(context: Context) : AbstractReportSession<SchedulerReportFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, ISchedulerReportsEntryPoint::class.java)
    private lateinit var resumeData: SchedulersResumeSessionReportTuple

    override suspend fun prepare(filter: SchedulerReportFilter) {
        super.prepare(filter)

        this.title = context.getString(R.string.scheduler_report_resume_session_title)
        this.resumeData = entryPoint.getSchedulerReportsRepository().getSchedulerReportResume(filter)

        this.components = listOf(
            LayoutGridComponent(
                columnCount = 3,
                items = listOf(
                    context.getString(R.string.schedulers_report_resume_session_label_professional) to resumeData.personName,
                    context.getString(R.string.schedulers_report_resume_session_label_pending) to resumeData.countPending.toString(),
                    context.getString(R.string.schedulers_report_resume_session_label_confirmed) to resumeData.countConfirmed.toString(),
                    context.getString(R.string.schedulers_report_resume_session_label_cancelled) to resumeData.countCancelled.toString(),
                    context.getString(R.string.schedulers_report_resume_session_label_completed) to resumeData.countCompleted.toString()
                )
            )
        )
    }
}