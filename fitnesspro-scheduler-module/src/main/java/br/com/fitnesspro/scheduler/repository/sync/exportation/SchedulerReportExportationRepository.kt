package br.com.fitnesspro.scheduler.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.ReportWebClient
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class SchedulerReportExportationRepository(
    context: Context,
    private val schedulerReportDAO: SchedulerReportDAO,
    private val reportWebClient: ReportWebClient
): AbstractExportationRepository<SchedulerReport, SchedulerReportDAO>(context) {

    override suspend fun getExportationData(pageInfos: ExportPageInfos): List<SchedulerReport> {
        return schedulerReportDAO.getExportationData(pageInfos)
    }

    override suspend fun callExportationService(
        modelList: List<SchedulerReport>,
        token: String
    ): ExportationServiceResponse {
        return reportWebClient.saveSchedulerReportBatch(
            token = token,
            schedulerReports = modelList
        )
    }

    override fun getOperationDAO() = schedulerReportDAO

}