package br.com.fitnesspro.scheduler.reports.schedules.sessions

import android.content.Context
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE_TIME_SHORT
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.pdf.generator.components.table.Column
import br.com.fitnesspro.pdf.generator.components.table.TableComponent
import br.com.fitnesspro.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.scheduler.reports.injection.ISchedulerReportsEntryPoint
import dagger.hilt.android.EntryPointAccessors
import java.time.ZoneId
import br.com.fitnesspro.scheduler.R as SchedulerRes

class SchedulerReportPendingSchedulesSession(context: Context) : AbstractReportSession<SchedulerReportFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, ISchedulerReportsEntryPoint::class.java)

    override suspend fun prepare(filter: SchedulerReportFilter) {
        super.prepare(filter)

        this.title = context.getString(SchedulerRes.string.scheduler_report_pending_schedules_session_title)

        val tableData = entryPoint.getSchedulerReportsRepository().getListSchedulerReportTuple(
            filter = filter,
            situation = EnumSchedulerSituation.SCHEDULED
        )

        this.components = listOf(
            TableComponent<SchedulerReportFilter>(
                columns = listOf(
                    Column(
                        label = context.getString(SchedulerRes.string.scheduler_report_pending_schedules_session_column_name),
                        widthPercent = 0.4f
                    ),
                    Column(
                        label = context.getString(SchedulerRes.string.scheduler_report_pending_schedules_session_column_datetime),
                        widthPercent = 0.4f
                    ),
                    Column(
                        label = context.getString(SchedulerRes.string.scheduler_report_pending_schedules_session_column_type),
                        widthPercent = 0.2f
                    )
                ),
                rows = tableData.map { tuple ->
                    val start = tuple.dateTimeStart.format(DATE_TIME_SHORT, ZoneId.systemDefault())
                    val end = tuple.dateTimeEnd.format(DATE_TIME_SHORT, ZoneId.systemDefault())

                    listOf(
                        tuple.personName,
                        context.getString(SchedulerRes.string.scheduler_report_label_period, start, end),
                        tuple.compromiseType.getLabel(context)!!
                    )
                }
            )
        )
    }
}