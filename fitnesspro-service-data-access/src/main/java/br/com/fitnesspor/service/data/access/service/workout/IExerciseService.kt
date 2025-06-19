package br.com.fitnesspor.service.data.access.service.workout

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IExerciseService {

    @POST(EndPointsV1.EXERCISE)
    suspend fun saveExercise(
        @Header("Authorization") token: String,
        @Body exerciseDTO: ExerciseDTO
    ): Response<PersistenceServiceResponse<ExerciseDTO>>
}