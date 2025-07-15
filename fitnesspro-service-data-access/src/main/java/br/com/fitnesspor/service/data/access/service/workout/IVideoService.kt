package br.com.fitnesspor.service.data.access.service.workout

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.VIDEO
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.VIDEO_EXECUTION
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.VIDEO_EXERCISE
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.VIDEO_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.VIDEO_IMPORT
import br.com.fitnesspro.shared.communication.dtos.workout.NewVideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.NewVideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface IVideoService {

    @GET("$VIDEO$VIDEO_IMPORT")
    suspend fun importVideos(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<VideoDTO>>

    @POST("$VIDEO$VIDEO_EXPORT")
    suspend fun saveVideoBatch(
        @Header("Authorization") token: String,
        @Body videoDTOList: List<VideoDTO>
    ): Response<ExportationServiceResponse>

    @POST("$VIDEO$VIDEO_EXERCISE")
    suspend fun createExerciseVideo(
        @Header("Authorization") token: String,
        @Body newVideoExerciseDTO: NewVideoExerciseDTO
    ): Response<PersistenceServiceResponse<NewVideoExerciseDTO>>

    @POST("$VIDEO$VIDEO_EXECUTION")
    suspend fun createExecutionVideo(
        @Header("Authorization") token: String,
        @Body newVideoExecutionDTO: NewVideoExerciseExecutionDTO
    ): Response<PersistenceServiceResponse<NewVideoExerciseExecutionDTO>>
}