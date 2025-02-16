package br.com.fitnesspor.service.data.access.service.workout

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IWorkoutService {

    @POST(EndPointsV1.WORKOUT)
    suspend fun saveWorkout(
        @Header("Authorization") token: String,
        @Body workoutDTO: WorkoutDTO
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.WORKOUT}/${EndPointsV1.WORKOUT_EXPORT}")
    suspend fun saveWorkoutBatch(
        @Header("Authorization") token: String,
        @Body workoutDTOList: List<WorkoutDTO>
    ): Response<PersistenceServiceResponse>

    @POST(EndPointsV1.WORKOUT_GROUP)
    suspend fun saveWorkoutGroup(
        @Header("Authorization") token: String,
        @Body workoutGroupDTO: WorkoutGroupDTO
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.WORKOUT_GROUP}/${EndPointsV1.WORKOUT_GROUP_EXPORT}")
    suspend fun saveWorkoutGroupBatch(
        @Header("Authorization") token: String,
        @Body workoutGroupDTOList: List<WorkoutGroupDTO>
    ): Response<PersistenceServiceResponse>
}