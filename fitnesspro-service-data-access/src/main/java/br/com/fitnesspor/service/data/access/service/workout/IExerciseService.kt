package br.com.fitnesspor.service.data.access.service.workout

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_EXECUTION_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_EXECUTION_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_EXECUTION_VIDEO_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_EXECUTION_VIDEO_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_PREDEFINITION_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_PREDEFINITION_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_PREDEFINITION_VIDEO_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_PREDEFINITION_VIDEO_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_VIDEO_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.EXERCISE_VIDEO_IMPORT
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface IExerciseService {

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

    @POST("$EXERCISE$EXERCISE_EXECUTION_EXPORT")
    suspend fun saveExerciseExecutionBatch(
        @Header("Authorization") token: String,
        @Body exerciseDTOs: List<ExerciseExecutionDTO>
    ): Response<ExportationServiceResponse>

    @GET("$EXERCISE$EXERCISE_EXECUTION_IMPORT")
    suspend fun importExerciseExecution(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<ExerciseExecutionDTO>>

    @GET("$EXERCISE$EXERCISE_EXECUTION_VIDEO_IMPORT")
    suspend fun importVideoExerciseExecution(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<VideoExerciseExecutionDTO>>

    @POST("$EXERCISE$EXERCISE_EXECUTION_VIDEO_EXPORT")
    suspend fun saveExerciseExecutionVideosBatch(
        @Header("Authorization") token: String,
        @Body videoDTOs: List<VideoExerciseExecutionDTO>
    ): Response<ExportationServiceResponse>

    @POST("$EXERCISE$EXERCISE_PREDEFINITION_EXPORT")
    suspend fun saveExercisePreDefinitionBatch(
        @Header("Authorization") token: String,
        @Body exerciseDTOs: List<ExercisePreDefinitionDTO>
    ): Response<ExportationServiceResponse>

    @POST("$EXERCISE$EXERCISE_PREDEFINITION_VIDEO_EXPORT")
    suspend fun saveExercisePreDefinitionVideosBatch(
        @Header("Authorization") token: String,
        @Body videoDTOs: List<VideoExercisePreDefinitionDTO>
    ): Response<ExportationServiceResponse>

    @GET("$EXERCISE$EXERCISE_PREDEFINITION_IMPORT")
    suspend fun importExercisePreDefinition(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<ExercisePreDefinitionDTO>>

    @GET("$EXERCISE$EXERCISE_PREDEFINITION_VIDEO_IMPORT")
    suspend fun importVideoExercisePreDefinition(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<VideoExercisePreDefinitionDTO>>
}