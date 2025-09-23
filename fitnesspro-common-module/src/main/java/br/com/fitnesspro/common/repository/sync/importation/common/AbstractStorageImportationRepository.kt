package br.com.fitnesspro.common.repository.sync.importation.common

import android.util.Log
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.base.FileModel
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.base.StorageModel
import br.com.fitnesspro.model.enums.EnumDownloadState
import br.com.fitnesspro.shared.communication.enums.storage.EnumGCBucketNames
import java.io.File

abstract class AbstractStorageImportationRepository<MODEL>(
    private val storageService: StorageBucketService
) where MODEL : StorageModel, MODEL : FileModel, MODEL : IntegratedModel {

    protected abstract suspend fun getModelsDownload(pageSize: Int): List<MODEL>

    abstract suspend fun getExistsModelsDownload(): Boolean

    protected abstract suspend fun createFiles(models: List<MODEL>): List<File>

    protected abstract fun getBucketName(): EnumGCBucketNames

    protected abstract fun getPageSize(): Int

    protected abstract fun getIntegratedMaintenanceDAO(): IntegratedMaintenanceDAO<MODEL>

    suspend fun import(): Boolean {
        Log.i(LogConstants.WORKER_IMPORT, "Importando ${javaClass.simpleName}")

        val models = getModelsDownload(getPageSize())
        val hasData: Boolean = models.isNotEmpty()

        if (hasData) {
            val files = createFiles(models)
            val urls = mutableListOf<String>()

            models.forEach {
                urls.add(it.storageUrl!!)
                it.storageDownloadState = EnumDownloadState.RUNNING
            }

            getIntegratedMaintenanceDAO().updateBatch(models, false)

            storageService.downloadAllByUrl(
                storageBucket = getBucketName().value,
                urls = urls,
                files = files
            )

            models.forEach {
                it.storageDownloadState = EnumDownloadState.DOWNLOADED
            }

            getIntegratedMaintenanceDAO().updateBatch(models, false)
        }

        return hasData
    }
}