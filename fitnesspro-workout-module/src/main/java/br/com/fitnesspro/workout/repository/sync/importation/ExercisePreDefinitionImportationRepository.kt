package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.mappers.getExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.ExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import java.time.LocalDateTime

class ExercisePreDefinitionImportationRepository(
    context: Context,
    private val exercisePredefinitionDAO: ExercisePreDefinitionDAO,
    private val webClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<ExercisePreDefinitionDTO, ExercisePreDefinition, ExercisePreDefinitionDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<ExercisePreDefinitionDTO> {
        return webClient.importExercisePreDefinition(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return exercisePredefinitionDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: ExercisePreDefinitionDTO): ExercisePreDefinition {
        return dto.getExercisePreDefinition()
    }

    override fun getOperationDAO(): ExercisePreDefinitionDAO {
        return exercisePredefinitionDAO
    }

}