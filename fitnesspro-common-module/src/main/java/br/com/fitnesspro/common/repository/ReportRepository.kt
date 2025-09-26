package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutReportDAO
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.to.TOReport

class ReportRepository(
    context: Context,
    private val reportDAO: ReportDAO,
    private val schedulerReportDAO: SchedulerReportDAO,
    private val personRepository: PersonRepository,
    private val workoutReportDAO: WorkoutReportDAO
): FitnessProRepository(context) {

    suspend fun getListReports(context: EnumReportContext, quickFilter: String? = null): List<TOReport> {
        val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

        return reportDAO.getListGeneratedReports(context, authenticatedPersonId, quickFilter)
    }

    suspend fun inactivateReport(context: EnumReportContext, reportId: String) {
        val report = reportDAO.getReportById(reportId)!!.apply {
            active = false
        }

        reportDAO.update(report, true)

        when (context) {
            EnumReportContext.SCHEDULERS_REPORT -> {
                val schedulerReport = schedulerReportDAO.getSchedulerReportByReportId(reportId).apply {
                    active = false
                }

                schedulerReportDAO.update(schedulerReport, true)
            }

            EnumReportContext.WORKOUT_REGISTER_EVOLUTION -> {
                val workoutReport = workoutReportDAO.getWorkoutReportByReportId(reportId).apply {
                    active = false
                }

                workoutReportDAO.update(workoutReport, true)
            }
        }
    }

    suspend fun inactivateAllReports(context: EnumReportContext, reports: List<Report>) {
        val reportIds = mutableListOf<String>()

        val reportList = reports.onEach {
            it.active = false

            reportIds.add(it.id)
        }

        reportDAO.updateBatch(reportList, true)

        when (context) {
            EnumReportContext.SCHEDULERS_REPORT -> {
                val schedulerReportList = schedulerReportDAO.getSchedulerReportByReportIdIn(reportIds).onEach {
                    it.active = false
                }

                schedulerReportDAO.updateBatch(schedulerReportList, true)
            }

            EnumReportContext.WORKOUT_REGISTER_EVOLUTION -> {
                val workoutReportList = workoutReportDAO.getWorkoutReportByReportIdIn(reportIds).onEach {
                    it.active = false
                }

                workoutReportDAO.updateBatch(workoutReportList, true)
            }
        }
    }
}