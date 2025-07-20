package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.mappers.getExerciseExecution
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.workout.R
import java.time.LocalDateTime

class ExerciseExecutionImportationRepository(
    context: Context,
    private val exerciseExecutionDAO: ExerciseExecutionDAO,
    private val webClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<ExerciseExecutionDTO, ExerciseExecution, ExerciseExecutionDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<ExerciseExecutionDTO> {
        return webClient.importExerciseExecution(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return exerciseExecutionDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: ExerciseExecutionDTO): ExerciseExecution {
        return dto.getExerciseExecution()
    }

    override fun getOperationDAO(): ExerciseExecutionDAO {
        return exerciseExecutionDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.exercise_execution_importation_descrition)
    }

    override fun getModule(): EnumSyncModule {
        return EnumSyncModule.WORKOUT
    }
}