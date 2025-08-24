package br.com.fitnesspro.common.repository.sync.importation.storage

import android.content.Context
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractStorageImportationRepository
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.shared.communication.enums.storage.EnumGCBucketNames
import java.io.File
import java.time.LocalDateTime

class VideoStorageImportationRepository(
    private val context: Context,
    private val videoDAO: VideoDAO,
    storageService: StorageBucketService
): AbstractStorageImportationRepository<Video>(storageService) {

    override suspend fun getModelsDownload(lastUpdateDate: LocalDateTime?): List<Video> {
        return videoDAO.getStorageImportationData(lastUpdateDate)
    }

    override suspend fun getExistsModelsDownload(lastUpdateDate: LocalDateTime?): Boolean {
        return videoDAO.getExistsStorageImportationData(lastUpdateDate)
    }

    override fun getBucketName(): EnumGCBucketNames {
        return EnumGCBucketNames.VIDEO
    }

    override suspend fun createFiles(models: List<Video>): List<File> {
        return models.map {
            val fileNameWithExtension = FileUtils.getFileNameWithExtensionFromFilePath(it.filePath!!)
            val fileName = FileUtils.getFileNameWithoutExtension(fileNameWithExtension)

            VideoUtils.createVideoFile(context, fileName)
        }
    }
}