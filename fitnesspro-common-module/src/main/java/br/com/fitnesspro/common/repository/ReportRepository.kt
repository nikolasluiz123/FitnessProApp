package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.ReportWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.to.TOReport

class ReportRepository(
    context: Context,
    private val reportDAO: ReportDAO,
    private val personRepository: PersonRepository,
    private val reportWebClient: ReportWebClient
): FitnessProRepository(context) {

    suspend fun getListReports(context: EnumReportContext, quickFilter: String? = null): List<TOReport> {
        val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

        return reportDAO.getListGeneratedReports(context, authenticatedPersonId, quickFilter)
    }

    suspend fun deleteSchedulerReport(reportId: String) {
        runInTransaction {
            deleteReportLocally(reportId)
            deleteReportRemote(reportId)
        }
    }

    private suspend fun deleteReportRemote(reportId: String) {
        reportWebClient.deleteSchedulerReport(
            token = getValidToken(),
            reportId = reportId
        )
    }

    private suspend fun deleteReportLocally(reportId: String) {
        reportDAO.getReportById(reportId)?.let { report ->
            reportDAO.deleteReport(report)
            FileUtils.deleteFile(report.filePath!!)
        }
    }

    suspend fun deleteAllReports(reports: List<Report>) {
        runInTransaction {
            deleteReportsLocally(reports)
            deleteReportsRemote()
        }

    }

    private suspend fun deleteReportsRemote() {
        reportWebClient.deleteAllSchedulerReport(getValidToken())
    }

    private suspend fun deleteReportsLocally(reports: List<Report>) {
        reportDAO.deleteReports(reports)

        reports.forEach { report ->
            FileUtils.deleteFile(report.filePath!!)
        }
    }
}