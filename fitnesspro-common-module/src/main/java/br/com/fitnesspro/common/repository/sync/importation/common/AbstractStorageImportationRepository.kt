package br.com.fitnesspro.common.repository.sync.importation.common

import android.util.Log
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import br.com.fitnesspro.model.base.FileModel
import br.com.fitnesspro.model.base.StorageModel
import br.com.fitnesspro.shared.communication.enums.storage.EnumGCBucketNames
import java.io.File
import java.time.LocalDateTime

abstract class AbstractStorageImportationRepository<MODEL>(
    private val storageService: StorageBucketService
) where MODEL : StorageModel, MODEL : FileModel {

    abstract suspend fun getModelsDownload(lastUpdateDate: LocalDateTime?): List<MODEL>

    abstract suspend fun getExistsModelsDownload(lastUpdateDate: LocalDateTime?): Boolean

    abstract suspend fun createFiles(models: List<MODEL>): List<File>

    abstract fun getBucketName(): EnumGCBucketNames

    suspend fun import(lastUpdateDate: LocalDateTime?) {
        Log.i(LogConstants.WORKER_IMPORT, "Importando ${javaClass.simpleName}")

        val models = getModelsDownload(lastUpdateDate)

        if (models.isNotEmpty()) {
            val files = createFiles(models)
            val urls = models.map { it.storageUrl!! }

            storageService.downloadAllByUrl(
                storageBucket = getBucketName().value,
                urls = urls,
                files = files
            )
        }
    }
}