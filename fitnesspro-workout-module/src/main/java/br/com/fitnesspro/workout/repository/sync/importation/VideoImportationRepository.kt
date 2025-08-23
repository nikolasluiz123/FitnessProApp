package br.com.fitnesspro.workout.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.mappers.getVideo
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.WorkoutModuleImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import java.time.LocalDateTime

class VideoImportationRepository(
    context: Context,
    private val videoDAO: VideoDAO,
    private val webClient: ExerciseWebClient,
    private val personRepository: PersonRepository
): AbstractImportationRepository<VideoDTO, Video, VideoDAO, WorkoutModuleImportFilter>(context) {

    override suspend fun getImportationData(
        token: String,
        filter: WorkoutModuleImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<VideoDTO> {
        return webClient.importVideos(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): WorkoutModuleImportFilter {
        val person = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!)
        return WorkoutModuleImportFilter(lastUpdateDate = lastUpdateDate, personId = person.id)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return videoDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: VideoDTO): Video {
        return dto.getVideo()
    }

    override fun getOperationDAO(): VideoDAO {
        return videoDAO
    }

}