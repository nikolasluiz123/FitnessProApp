package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateWorkoutGroupPreDefinitionUseCase(
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository
) {

    suspend operator fun invoke(workoutGroupPreDefinitionId: String) = withContext(IO) {
        exercisePreDefinitionRepository.runInTransaction {
            exercisePreDefinitionRepository.inactivateWorkoutGroupPreDefinition(workoutGroupPreDefinitionId)
        }
    }
}