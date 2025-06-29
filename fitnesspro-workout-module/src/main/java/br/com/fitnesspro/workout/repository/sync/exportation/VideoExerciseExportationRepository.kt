package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoExerciseDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.R

class VideoExerciseExportationRepository(
    context: Context,
    private val videoExerciseDAO: VideoExerciseDAO,
    private val exerciseWebClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<VideoExercise, VideoExerciseDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<VideoExercise> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return videoExerciseDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): VideoExerciseDAO {
        return videoExerciseDAO
    }

    override suspend fun callExportationService(
        modelList: List<VideoExercise>,
        token: String
    ): ExportationServiceResponse {
        return exerciseWebClient.saveExerciseVideoBatch(token, modelList)
    }

    override fun getDescription(): String {
        return context.getString(R.string.video_exercise_exportation_description)
    }

    override fun getModule() = EnumSyncModule.WORKOUT
}