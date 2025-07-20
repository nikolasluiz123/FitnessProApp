package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.R

class ExercisePreDefinitionExportationRepository(
    context: Context,
    private val exercisePreDefinitionDAO: ExercisePreDefinitionDAO,
    private val exerciseWebClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<ExercisePreDefinition, ExercisePreDefinitionDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<ExercisePreDefinition> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return exercisePreDefinitionDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): ExercisePreDefinitionDAO {
        return exercisePreDefinitionDAO
    }

    override suspend fun callExportationService(
        modelList: List<ExercisePreDefinition>,
        token: String
    ): ExportationServiceResponse {
        return exerciseWebClient.saveExercisePreDefinitionBatch(token, modelList)
    }

    override fun getDescription(): String {
        return context.getString(R.string.exercise_pre_definition_exportation_description)
    }

    override fun getModule() = EnumSyncModule.WORKOUT
}