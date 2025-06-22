package br.com.fitnesspor.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.general.IReportService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.mappers.getReportDTO
import br.com.fitnesspro.mappers.getSchedulerReportDTO
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.shared.communication.dtos.general.ReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.SchedulerReportDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.SchedulerReportImportFilter
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import com.google.gson.GsonBuilder

class ReportWebClient(
    context: Context,
    private val reportService: IReportService,
): FitnessProWebClient(context) {

    suspend fun saveSchedulerReport(token: String, report: Report, schedulerReport: SchedulerReport): PersistenceServiceResponse<SchedulerReportDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                val dto = schedulerReport.getSchedulerReportDTO(report.getReportDTO())

                reportService.saveSchedulerReport(
                    token = formatToken(token),
                    schedulerReportDTO = dto
                ).getResponseBody(SchedulerReportDTO::class.java)
            }
        )
    }

    suspend fun importReportsFromScheduler(
        token: String,
        filter: SchedulerReportImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<ReportDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                reportService.importReportsFromScheduler(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(ReportDTO::class.java)
            }
        )
    }

    suspend fun importSchedulerReports(
        token: String,
        filter: SchedulerReportImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<SchedulerReportDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                reportService.importSchedulerReports(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(SchedulerReportDTO::class.java)
            }
        )
    }

    suspend fun deleteSchedulerReport(token: String, reportId: String): FitnessProServiceResponse {
        return serviceErrorHandlingBlock(
            codeBlock = {
                reportService.deleteSchedulerReport(
                    token = formatToken(token),
                    reportId = reportId
                ).getResponseBody()
            }
        )
    }

    suspend fun deleteAllSchedulerReport(token: String): FitnessProServiceResponse {
        return serviceErrorHandlingBlock(
            codeBlock = {
                reportService.deleteAllSchedulerReport(token = formatToken(token)).getResponseBody()
            }
        )
    }
}