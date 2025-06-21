package br.com.fitnesspro.scheduler.reports.schedules.sessions

import android.content.Context
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.pdf.generator.components.table.Column
import br.com.fitnesspro.pdf.generator.components.table.TableComponent
import br.com.fitnesspro.pdf.generator.session.AbstractReportSession
import br.com.fitnesspro.scheduler.reports.injection.ISchedulerReportsEntryPoint
import br.com.fitnesspro.tuple.reports.schedulers.SchedulerReportTuple
import dagger.hilt.android.EntryPointAccessors
import java.time.ZoneId
import br.com.fitnesspro.scheduler.R as SchedulerRes

class SchedulerReportCompletedSchedulesSession(context: Context) : AbstractReportSession<SchedulerReportFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, ISchedulerReportsEntryPoint::class.java)
    private var tableData: List<SchedulerReportTuple> = emptyList()

    override suspend fun prepare(filter: SchedulerReportFilter) {
        super.prepare(filter)

        this.title = context.getString(SchedulerRes.string.scheduler_report_completed_schedules_session_title)

        tableData = entryPoint.getSchedulerReportsRepository().getListSchedulerReportTuple(
            filter = filter,
            situation = EnumSchedulerSituation.COMPLETED
        )

        this.components = listOf(
            TableComponent<SchedulerReportFilter>(
                columns = listOf(
                    Column(
                        label = context.getString(SchedulerRes.string.scheduler_report_completed_schedules_session_column_name),
                        widthPercent = 0.4f
                    ),
                    Column(
                        label = context.getString(SchedulerRes.string.scheduler_report_completed_schedules_session_column_date),
                        widthPercent = 0.15f
                    ),
                    Column(
                        label = context.getString(SchedulerRes.string.scheduler_report_completed_schedules_session_column_time),
                        widthPercent = 0.2f
                    ),
                    Column(
                        label = context.getString(SchedulerRes.string.scheduler_report_completed_schedules_session_column_type),
                        widthPercent = 0.25f
                    )
                ),
                rows = tableData.map { tuple ->
                    val date = tuple.dateTimeStart.format(DATE, ZoneId.systemDefault())
                    val timeStart = tuple.dateTimeStart.format(TIME, ZoneId.systemDefault())
                    val timeEnd = tuple.dateTimeEnd.format(TIME, ZoneId.systemDefault())

                    listOf(
                        tuple.personName,
                        date,
                        context.getString(SchedulerRes.string.scheduler_report_label_period, timeStart, timeEnd),
                        tuple.compromiseType.getLabel(context)!!
                    )
                }
            )
        )
    }

    override fun shouldRender(filter: SchedulerReportFilter): Boolean {
        return tableData.isNotEmpty()
    }
}