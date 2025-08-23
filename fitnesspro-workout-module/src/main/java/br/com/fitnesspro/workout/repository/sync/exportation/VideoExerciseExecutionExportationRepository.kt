package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class VideoExerciseExecutionExportationRepository(
    context: Context,
    private val videoExerciseExecutionDAO: VideoExerciseExecutionDAO,
    private val exerciseWebClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<VideoExerciseExecution, VideoExerciseExecutionDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<VideoExerciseExecution> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return videoExerciseExecutionDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): VideoExerciseExecutionDAO {
        return videoExerciseExecutionDAO
    }

    override suspend fun callExportationService(
        modelList: List<VideoExerciseExecution>,
        token: String
    ): ExportationServiceResponse {
        return exerciseWebClient.saveExerciseExecutionVideosBatch(token, modelList)
    }

}