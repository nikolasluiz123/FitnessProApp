package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class VideoExercisePreDefinitionExportationRepository(
    context: Context,
    private val videoExercisePreDefinitionDAO: VideoExercisePreDefinitionDAO,
    private val exerciseWebClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<VideoExercisePreDefinition, VideoExercisePreDefinitionDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<VideoExercisePreDefinition> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return videoExercisePreDefinitionDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): VideoExercisePreDefinitionDAO {
        return videoExercisePreDefinitionDAO
    }

    override suspend fun callExportationService(
        modelList: List<VideoExercisePreDefinition>,
        token: String
    ): ExportationServiceResponse {
        return exerciseWebClient.saveExercisePreDefinitionVideosBatch(token, modelList)
    }

}