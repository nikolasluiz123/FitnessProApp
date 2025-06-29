package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.mappers.getWorkout
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.workout.R
import java.time.LocalDateTime

class WorkoutImportationRepository(
    context: Context,
    private val workoutDAO: WorkoutDAO,
    private val webClient: WorkoutWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<WorkoutDTO, Workout, WorkoutDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<WorkoutDTO> {
        return webClient.importWorkouts(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return workoutDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: WorkoutDTO): Workout {
        return dto.getWorkout()
    }

    override fun getOperationDAO(): WorkoutDAO {
        return workoutDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.workout_importation_descrition)
    }

    override fun getModule(): EnumSyncModule {
        return EnumSyncModule.WORKOUT
    }
}