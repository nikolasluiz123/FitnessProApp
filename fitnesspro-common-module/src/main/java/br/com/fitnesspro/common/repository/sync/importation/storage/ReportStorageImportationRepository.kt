package br.com.fitnesspro.common.repository.sync.importation.storage

import android.content.Context
import br.com.android.pdf.generator.utils.ReportFileUtils
import br.com.core.android.utils.media.FileUtils
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractStorageImportationRepository
import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.shared.communication.enums.storage.EnumGCBucketNames
import java.io.File

class ReportStorageImportationRepository(
    private val context: Context,
    private val reportDAO: ReportDAO,
    storageService: StorageBucketService
): AbstractStorageImportationRepository<Report>(storageService) {

    override fun getPageSize(): Int = 10

    override fun getIntegratedMaintenanceDAO() = reportDAO

    override suspend fun getModelsDownload(pageSize: Int): List<Report> {
        return reportDAO.getStorageImportationData(pageSize)
    }

    override suspend fun getExistsModelsDownload(): Boolean {
        return reportDAO.getExistsStorageImportationData()
    }

    override fun getBucketName(): EnumGCBucketNames {
        return EnumGCBucketNames.REPORT
    }

    override suspend fun createFiles(models: List<Report>): List<File> {
        return models.map {
            val fileNameWithExtension = FileUtils.getFileNameWithExtensionFromFilePath(it.filePath!!)
            val fileName = FileUtils.getFileNameWithoutExtension(fileNameWithExtension)

            ReportFileUtils.createReportFile(context, fileName)
        }
    }
}