package br.com.fitnesspro.scheduler.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.ReportWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class ReportFromSchedulerExportationRepository(
    context: Context,
    private val reportDAO: ReportDAO,
    private val reportWebClient: ReportWebClient
): AbstractExportationRepository<Report, ReportDAO>(context) {

    override suspend fun getExportationData(pageInfos: ExportPageInfos): List<Report> {
        return reportDAO.getExportationData(EnumReportContext.SCHEDULERS_REPORT, pageInfos)
    }

    override suspend fun callExportationService(
        modelList: List<Report>,
        token: String
    ): ExportationServiceResponse {
        return reportWebClient.saveReportBatch(
            token = token,
            reports = modelList
        )
    }

    override fun getOperationDAO() = reportDAO

    override fun getDescription() = context.getString(R.string.sync_module_report_from_scheduler)

    override fun getModule() = EnumSyncModule.SCHEDULER
}