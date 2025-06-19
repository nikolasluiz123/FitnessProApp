package br.com.fitnesspor.service.data.access.webclient.workout

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.workout.IExerciseService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.mappers.getExerciseDTO
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO

class ExerciseWebClient(
    context: Context,
    private val exerciseService: IExerciseService,
): FitnessProWebClient(context) {

    suspend fun saveExercise(token: String, exercise: Exercise, workoutGroup: WorkoutGroup) {
        persistenceServiceErrorHandlingBlock(
            codeBlock = {
                exerciseService.saveExercise(
                    token = formatToken(token),
                    exerciseDTO = exercise.getExerciseDTO(workoutGroup)
                ).getResponseBody(ExerciseDTO::class.java)
            }
        )
    }
}