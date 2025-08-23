package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class VideoExportationRepository(
    context: Context,
    private val videoDAO: VideoDAO,
    private val exerciseWebClient: ExerciseWebClient
): AbstractExportationRepository<Video, VideoDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<Video> {
        return videoDAO.getExportationData(pageInfos)
    }

    override fun getOperationDAO(): VideoDAO {
        return videoDAO
    }

    override suspend fun callExportationService(
        modelList: List<Video>,
        token: String
    ): ExportationServiceResponse {
        return exerciseWebClient.saveVideoBatch(token, modelList)
    }

}