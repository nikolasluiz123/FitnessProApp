package br.com.fitnesspor.service.data.access.service.workout

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_VIDEO_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_VIDEO_IMPORT
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface IExerciseService {

    @POST(EXERCISE)
    suspend fun saveExercise(
        @Header("Authorization") token: String,
        @Body exerciseDTO: ExerciseDTO
    ): Response<PersistenceServiceResponse<ExerciseDTO>>

    @GET("$EXERCISE$EXERCISE_IMPORT")
    suspend fun importExercise(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<ExerciseDTO>>

    @GET("$EXERCISE$EXERCISE_VIDEO_IMPORT")
    suspend fun importExerciseVideos(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<VideoExerciseDTO>>

    @POST("$EXERCISE$EXERCISE_EXPORT")
    suspend fun saveExerciseBatch(
        @Header("Authorization") token: String,
        @Body exerciseDTOList: List<ExerciseDTO>
    ): Response<ExportationServiceResponse>

    @POST("$EXERCISE$EXERCISE_VIDEO_EXPORT")
    suspend fun saveExerciseVideoBatch(
        @Header("Authorization") token: String,
        @Body exerciseVideoDTOList: List<VideoExerciseDTO>
    ): Response<ExportationServiceResponse>
}