package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import br.com.fitnesspro.mappers.getWorkoutGroupPreDefinition
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupPreDefinitionDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.workout.R
import java.time.LocalDateTime

class WorkoutGroupPreDefinitionImportationRepository(
    context: Context,
    private val workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO,
    private val webClient: WorkoutWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<WorkoutGroupPreDefinitionDTO, WorkoutGroupPreDefinition, WorkoutGroupPreDefinitionDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<WorkoutGroupPreDefinitionDTO> {
        return webClient.importWorkoutGroupsPreDefinition(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return workoutGroupPreDefinitionDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: WorkoutGroupPreDefinitionDTO): WorkoutGroupPreDefinition {
        return dto.getWorkoutGroupPreDefinition()
    }

    override fun getOperationDAO(): WorkoutGroupPreDefinitionDAO {
        return workoutGroupPreDefinitionDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.workout_group_pre_definition_importation_descrition)
    }

    override fun getModule(): EnumSyncModule {
        return EnumSyncModule.WORKOUT
    }
}