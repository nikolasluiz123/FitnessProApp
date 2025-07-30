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
import br.com.fitnesspro.shared.communication.query.filter.importation.ReportImportFilter
import br.com.fitnesspro.shared.communication.query.filter.importation.SchedulerReportImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import com.google.gson.GsonBuilder

class ReportWebClient(
    context: Context,
    private val reportService: IReportService,
): FitnessProWebClient(context) {

    suspend fun saveSchedulerReportBatch(token: String, schedulerReports: List<SchedulerReport>): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val schedulerReportDTOList = schedulerReports.map(SchedulerReport::getSchedulerReportDTO)

                reportService.saveSchedulerReportBatch(
                    token = formatToken(token),
                    schedulerReportDTOList = schedulerReportDTOList
                ).getResponseBody()
            }
        )
    }

    suspend fun saveReportBatch(token: String, reports: List<Report>): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val reportDTOList = reports.map(Report::getReportDTO)

                reportService.saveReportBatch(
                    token = formatToken(token),
                    reportDTOList = reportDTOList
                ).getResponseBody()
            }
        )
    }

    suspend fun importReports(
        token: String,
        filter: ReportImportFilter,
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
}