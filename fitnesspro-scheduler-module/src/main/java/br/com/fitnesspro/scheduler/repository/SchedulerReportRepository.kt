package br.com.fitnesspro.scheduler.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.mappers.getSchedulerReport
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.to.TOReport
import br.com.fitnesspro.to.TOSchedulerReport
import br.com.fitnesspro.tuple.reports.schedulers.SchedulerReportTuple
import br.com.fitnesspro.tuple.reports.schedulers.SchedulersResumeSessionReportTuple

class SchedulerReportRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO,
    private val schedulerReportDAO: SchedulerReportDAO,
    private val reportDAO: ReportDAO
): FitnessProRepository(context) {

    suspend fun getSchedulerReportResume(filter: SchedulerReportFilter): SchedulersResumeSessionReportTuple {
        return schedulerDAO.getSchedulerReportResume(filter)
    }

    suspend fun getListSchedulerReportTuple(filter: SchedulerReportFilter, situation: EnumSchedulerSituation): List<SchedulerReportTuple> {
        return schedulerDAO.getListSchedulerReportTuple(filter, situation)
    }

    suspend fun saveSchedulerReport(toReport: TOReport, toSchedulerReport: TOSchedulerReport) {
        runInTransaction {
            val report = toReport.getReport()
            val schedulerReport = toSchedulerReport.getSchedulerReport()
            schedulerReport.reportId = report.id

            if (toReport.id == null) {
                reportDAO.insert(report)
                schedulerReportDAO.insert(schedulerReport)
            } else {
                reportDAO.update(report)
                schedulerReportDAO.update(schedulerReport)
            }

            toReport.id = report.id
            toSchedulerReport.id = schedulerReport.id
        }
    }
}