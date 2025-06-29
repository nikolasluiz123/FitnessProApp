package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.R

class VideoExportationRepository(
    context: Context,
    private val videoDAO: VideoDAO,
    private val exerciseWebClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<Video, VideoDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<Video> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return videoDAO.getExportationData(pageInfos, personId)
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

    override fun getDescription(): String {
        return context.getString(R.string.video_exportation_description)
    }

    override fun getModule() = EnumSyncModule.WORKOUT
}