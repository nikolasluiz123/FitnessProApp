package br.com.fitnesspro.common.repository.sync.importation.storage

import android.content.Context
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractStorageImportationRepository
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.pdf.generator.utils.ReportFileUtils
import java.io.File
import java.time.LocalDateTime

class ReportStorageImportationRepository(
    private val context: Context,
    private val reportDAO: ReportDAO,
    storageService: StorageBucketService
): AbstractStorageImportationRepository<Report>(storageService) {

    override suspend fun getModelsDownload(lastUpdateDate: LocalDateTime?): List<Report> {
        return reportDAO.getStorageImportationData(lastUpdateDate)
    }

    override suspend fun getExistsModelsDownload(lastUpdateDate: LocalDateTime?): Boolean {
        return reportDAO.getExistsStorageImportationData(lastUpdateDate)
    }

    override suspend fun createFiles(models: List<Report>): List<File> {
        return models.map {
            val fileNameWithExtension = FileUtils.getFileNameWithExtensionFromFilePath(it.filePath!!)
            val fileName = FileUtils.getFileNameWithoutExtension(fileNameWithExtension)

            ReportFileUtils.createReportFile(context, fileName)
        }
    }
}