package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoExerciseDAO
import br.com.fitnesspro.mappers.getVideoExercise
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.workout.R
import java.time.LocalDateTime

class VideoExerciseImportationRepository(
    context: Context,
    private val videoExerciseDAO: VideoExerciseDAO,
    private val webClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<VideoExerciseDTO, VideoExercise, VideoExerciseDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoExerciseDTO> {
        return webClient.importExerciseVideos(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return videoExerciseDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: VideoExerciseDTO): VideoExercise {
        return dto.getVideoExercise()
    }

    override fun getOperationDAO(): VideoExerciseDAO {
        return videoExerciseDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.video_exercise_importation_descrition)
    }

    override fun getModule(): EnumSyncModule {
        return EnumSyncModule.WORKOUT
    }
}