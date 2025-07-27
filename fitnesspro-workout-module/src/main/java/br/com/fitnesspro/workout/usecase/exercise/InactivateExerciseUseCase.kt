package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.repository.ExerciseRepository

class InactivateExerciseUseCase(private val exerciseRepository: ExerciseRepository) {

    suspend operator fun invoke(exerciseId: String) {
        exerciseRepository.runInTransaction {
            exerciseRepository.inactivateExercise(exerciseId)
        }
    }

}