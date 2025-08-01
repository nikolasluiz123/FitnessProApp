package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.R

class ExerciseExportationRepository(
    context: Context,
    private val exerciseDAO: ExerciseDAO,
    private val exerciseWebClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<Exercise, ExerciseDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<Exercise> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return exerciseDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): ExerciseDAO {
        return exerciseDAO
    }

    override suspend fun callExportationService(
        modelList: List<Exercise>,
        token: String
    ): ExportationServiceResponse {
        return exerciseWebClient.saveExerciseBatch(token, modelList)
    }

    override fun getDescription(): String {
        return context.getString(R.string.exercise_exportation_description)
    }

    override fun getModule() = EnumSyncModule.WORKOUT
}