package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateExerciseExecutionUseCase(
    private val exerciseExecutionRepository: ExerciseExecutionRepository
) {

    suspend operator fun invoke(exerciseExecutionId: String) = withContext(IO) {
        exerciseExecutionRepository.runInTransaction {
            exerciseExecutionRepository.inactivateExerciseExecution(exerciseExecutionId)
        }
    }

}