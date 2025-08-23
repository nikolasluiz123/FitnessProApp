package br.com.fitnesspro.common.repository.sync.importation.common

import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import br.com.fitnesspro.model.base.FileModel
import br.com.fitnesspro.model.base.StorageModel
import java.io.File
import java.time.LocalDateTime

abstract class AbstractStorageImportationRepository<MODEL>(
    private val storageService: StorageBucketService
) where MODEL : StorageModel, MODEL : FileModel {

    abstract suspend fun getModelsDownload(lastUpdateDate: LocalDateTime?): List<MODEL>

    abstract suspend fun createFiles(models: List<MODEL>): List<File>

    suspend fun import(lastUpdateDate: LocalDateTime?) {
        val models = getModelsDownload(lastUpdateDate)

        if (models.isNotEmpty()) {
            val files = createFiles(models)
            val urls = models.map { it.storageUrl!! }

            storageService.downloadAllByUrl(urls, files)
        }
    }
}