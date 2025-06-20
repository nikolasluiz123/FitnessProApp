package br.com.fitnesspro.scheduler.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.filters.SchedulerReportFilter
import br.com.fitnesspro.tuple.SchedulersResumeTuple

class SchedulerReportRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO
): FitnessProRepository(context) {

    suspend fun getSchedulerReportResume(filter: SchedulerReportFilter): SchedulersResumeTuple {
        return schedulerDAO.getSchedulerReportResume(filter)
    }
}