package br.com.fitnesspor.service.data.access.service.workout

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_GROUP
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_GROUP_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_GROUP_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_GROUP_INACTIVATE
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_GROUP_PREDEFINITION_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_GROUP_PREDEFINITION_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.WORKOUT_INACTIVATE
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupPreDefinitionDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IWorkoutService {

    @POST(WORKOUT)
    suspend fun saveWorkout(
        @Header("Authorization") token: String,
        @Body workoutDTO: WorkoutDTO
    ): Response<PersistenceServiceResponse<WorkoutDTO>>

    @POST("$WORKOUT$WORKOUT_EXPORT")
    suspend fun saveWorkoutBatch(
        @Header("Authorization") token: String,
        @Body workoutDTOList: List<WorkoutDTO>
    ): Response<ExportationServiceResponse>

    @POST("$WORKOUT$WORKOUT_GROUP")
    suspend fun saveWorkoutGroup(
        @Header("Authorization") token: String,
        @Body workoutGroupDTO: WorkoutGroupDTO
    ): Response<PersistenceServiceResponse<WorkoutGroupDTO>>

    @POST("$WORKOUT$WORKOUT_GROUP_EXPORT")
    suspend fun saveWorkoutGroupBatch(
        @Header("Authorization") token: String,
        @Body workoutGroupDTOList: List<WorkoutGroupDTO>
    ): Response<ExportationServiceResponse>

    @GET("$WORKOUT$WORKOUT_IMPORT")
    suspend fun importWorkout(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<WorkoutDTO>>

    @GET("$WORKOUT$WORKOUT_GROUP_IMPORT")
    suspend fun importWorkoutGroup(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<WorkoutGroupDTO>>

    @PUT("$WORKOUT$WORKOUT_GROUP_INACTIVATE")
    suspend fun inactivateWorkoutGroup(
        @Header("Authorization") token: String,
        @Path("workoutGroupId") workoutGroupId: String
    ): Response<FitnessProServiceResponse>

    @PUT("$WORKOUT$WORKOUT_INACTIVATE")
    suspend fun inactivateWorkout(
        @Header("Authorization") token: String,
        @Path("workoutId") workoutId: String
    ): Response<FitnessProServiceResponse>

    @POST("$WORKOUT$WORKOUT_GROUP_PREDEFINITION_EXPORT")
    suspend fun saveWorkoutGroupPreDefinitionBatch(
        @Header("Authorization") token: String,
        @Body workoutGroupDTOList: List<WorkoutGroupPreDefinitionDTO>
    ): Response<ExportationServiceResponse>

    @GET("$WORKOUT$WORKOUT_GROUP_PREDEFINITION_IMPORT")
    suspend fun importWorkoutGroupPreDefinition(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<WorkoutGroupPreDefinitionDTO>>
}