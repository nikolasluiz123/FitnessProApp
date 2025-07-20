package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoExercisePreDefinitionDAO
import br.com.fitnesspro.mappers.getVideoExercisePreDefinition
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.workout.R
import java.time.LocalDateTime

class VideoExercisePreDefinitionImportationRepository(
    context: Context,
    private val videoExercisePreDefinitionDAO: VideoExercisePreDefinitionDAO,
    private val webClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<VideoExercisePreDefinitionDTO, VideoExercisePreDefinition, VideoExercisePreDefinitionDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoExercisePreDefinitionDTO> {
        return webClient.importVideoExercisePreDefinition(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return videoExercisePreDefinitionDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: VideoExercisePreDefinitionDTO): VideoExercisePreDefinition {
        return dto.getVideoExercisePreDefinition()
    }

    override fun getOperationDAO(): VideoExercisePreDefinitionDAO {
        return videoExercisePreDefinitionDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.video_exercise_pre_definition_importation_descrition)
    }

    override fun getModule(): EnumSyncModule {
        return EnumSyncModule.WORKOUT
    }
}