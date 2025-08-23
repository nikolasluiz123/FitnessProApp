package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.getWorkoutGroup
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import java.time.LocalDateTime

class WorkoutGroupImportationRepository(
    context: Context,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val webClient: WorkoutWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<WorkoutGroupDTO, WorkoutGroup, WorkoutGroupDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<WorkoutGroupDTO> {
        return webClient.importWorkoutGroups(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return workoutGroupDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: WorkoutGroupDTO): WorkoutGroup {
        return dto.getWorkoutGroup()
    }

    override fun getOperationDAO(): WorkoutGroupDAO {
        return workoutGroupDAO
    }

}