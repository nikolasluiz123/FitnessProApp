package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class WorkoutGroupPreDefinitionExportationRepository(
    context: Context,
    private val workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO,
    private val workoutWebClient: WorkoutWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<WorkoutGroupPreDefinition, WorkoutGroupPreDefinitionDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<WorkoutGroupPreDefinition> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return workoutGroupPreDefinitionDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): WorkoutGroupPreDefinitionDAO {
        return workoutGroupPreDefinitionDAO
    }

    override suspend fun callExportationService(
        modelList: List<WorkoutGroupPreDefinition>,
        token: String
    ): ExportationServiceResponse {
        return workoutWebClient.saveWorkoutGroupPreDefinitionBatch(token, modelList)
    }

}