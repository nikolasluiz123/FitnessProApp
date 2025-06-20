package br.com.fitnesspro.scheduler.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.tuple.reports.schedulers.SchedulerReportTuple
import br.com.fitnesspro.tuple.reports.schedulers.SchedulersResumeSessionReportTuple

class SchedulerReportRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO
): FitnessProRepository(context) {

    suspend fun getSchedulerReportResume(filter: SchedulerReportFilter): SchedulersResumeSessionReportTuple {
        return schedulerDAO.getSchedulerReportResume(filter)
    }

    suspend fun getListSchedulerReportTuple(filter: SchedulerReportFilter, situation: EnumSchedulerSituation): List<SchedulerReportTuple> {
        return schedulerDAO.getListSchedulerReportTuple(filter, situation)
    }
}