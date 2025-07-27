package br.com.fitnesspro.workout.usecase.workout

import br.com.fitnesspro.workout.repository.WorkoutRepository

class InactivateWorkoutUseCase(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workoutId: String) {
        workoutRepository.runInTransaction {
            workoutRepository.inactivateWorkout(workoutId)
        }
    }
}