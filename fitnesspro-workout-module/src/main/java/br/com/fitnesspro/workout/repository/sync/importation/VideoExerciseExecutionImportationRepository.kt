package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoExerciseExecutionDAO
import br.com.fitnesspro.mappers.getVideoExerciseExecution
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.workout.R
import java.time.LocalDateTime

class VideoExerciseExecutionImportationRepository(
    context: Context,
    private val videoExerciseExecutionDAO: VideoExerciseExecutionDAO,
    private val webClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<VideoExerciseExecutionDTO, VideoExerciseExecution, VideoExerciseExecutionDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoExerciseExecutionDTO> {
        return webClient.importVideoExerciseExecution(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return videoExerciseExecutionDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: VideoExerciseExecutionDTO): VideoExerciseExecution {
        return dto.getVideoExerciseExecution()
    }

    override fun getOperationDAO(): VideoExerciseExecutionDAO {
        return videoExerciseExecutionDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.video_exercise_execution_importation_descrition)
    }

    override fun getModule(): EnumSyncModule {
        return EnumSyncModule.WORKOUT
    }
}