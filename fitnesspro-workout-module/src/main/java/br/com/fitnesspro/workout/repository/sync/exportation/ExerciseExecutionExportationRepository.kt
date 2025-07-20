package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.R

class ExerciseExecutionExportationRepository(
    context: Context,
    private val exerciseExecutionDAO: ExerciseExecutionDAO,
    private val exerciseWebClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<ExerciseExecution, ExerciseExecutionDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<ExerciseExecution> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return exerciseExecutionDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): ExerciseExecutionDAO {
        return exerciseExecutionDAO
    }

    override suspend fun callExportationService(
        modelList: List<ExerciseExecution>,
        token: String
    ): ExportationServiceResponse {
        return exerciseWebClient.saveExerciseExecutionBatch(token, modelList)
    }

    override fun getDescription(): String {
        return context.getString(R.string.exercise_execution_exportation_description)
    }

    override fun getModule() = EnumSyncModule.WORKOUT
}