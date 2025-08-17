package br.com.fitnesspro.common.repository.sync.exportation.storage

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.storage.StorageWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractStorageExportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.shared.communication.responses.StorageServiceResponse
import java.io.File

class VideoStorageExportationRepository(
    private val videoDAO: VideoDAO,
    private val storageWebClient: StorageWebClient,
    context: Context
): AbstractStorageExportationRepository<Video, VideoDAO>(context) {

    override fun getPageSize(): Int = 5

    override suspend fun getExportationModels(pageInfos: ExportPageInfos): List<Video> {
        return videoDAO.getStorageExportationData(pageInfos)
    }

    override suspend fun callExportationService(
        modelIds: List<String>,
        files: List<File>,
        token: String
    ): StorageServiceResponse {
        return storageWebClient.uploadVideos(token, modelIds, files)
    }

    override fun getOperationDAO(): VideoDAO {
        return videoDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.video_storage_exportation_description)
    }

    override fun getModule(): EnumSyncModule {
        return EnumSyncModule.GENERAL
    }
}