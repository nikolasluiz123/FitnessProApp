package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateExerciseUseCase(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exerciseId: String) = withContext(IO) {
        exerciseRepository.runInTransaction {
            exerciseRepository.inactivateExercise(exerciseId)
        }
    }

}