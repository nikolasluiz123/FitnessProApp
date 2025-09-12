package br.com.fitnesspro.common.repository.sync.exportation.storage

import android.content.Context
import br.com.fitnesspro.service.data.access.webclient.storage.StorageWebClient
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractStorageExportationRepository
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.shared.communication.responses.StorageServiceResponse
import java.io.File

class ReportStorageExportationRepository(
    private val reportDAO: ReportDAO,
    private val storageWebClient: StorageWebClient,
    context: Context
): AbstractStorageExportationRepository<Report, ReportDAO>(context) {

    override fun getPageSize(): Int = 10

    override suspend fun getExportationModels(pageInfos: ExportPageInfos): List<Report> {
        return reportDAO.getStorageExportationData(pageInfos)
    }

    override suspend fun callExportationService(
        modelIds: List<String>,
        files: List<File>,
        token: String
    ): StorageServiceResponse {
        return storageWebClient.uploadReports(token, modelIds, files)
    }

    override fun getOperationDAO(): ReportDAO {
        return reportDAO
    }

}