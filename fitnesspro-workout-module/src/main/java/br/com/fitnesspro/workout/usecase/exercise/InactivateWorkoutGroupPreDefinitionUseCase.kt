package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository

class InactivateWorkoutGroupPreDefinitionUseCase(
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository
) {

    suspend operator fun invoke(workoutGroupPreDefinitionId: String) {
        exercisePreDefinitionRepository.runInTransaction {
            exercisePreDefinitionRepository.inactivateWorkoutGroupPreDefinition(workoutGroupPreDefinitionId)
        }
    }
}