package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateExercisePreDefinitionUseCase(
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository
) {

    suspend operator fun invoke(exercisePreDefinitionId: String) = withContext(IO) {
        exercisePreDefinitionRepository.runInTransaction {
            exercisePreDefinitionRepository.inactivateExercisePreDefinition(listOf(exercisePreDefinitionId))
        }
    }

}