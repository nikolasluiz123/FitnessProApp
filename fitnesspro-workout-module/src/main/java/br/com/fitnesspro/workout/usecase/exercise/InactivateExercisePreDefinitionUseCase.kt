package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository

class InactivateExercisePreDefinitionUseCase(
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository
) {

    suspend operator fun invoke(exercisePreDefinitionId: String) {
        exercisePreDefinitionRepository.runInTransaction {
            exercisePreDefinitionRepository.inactivateExercisePreDefinition(exercisePreDefinitionId)
        }
    }

}