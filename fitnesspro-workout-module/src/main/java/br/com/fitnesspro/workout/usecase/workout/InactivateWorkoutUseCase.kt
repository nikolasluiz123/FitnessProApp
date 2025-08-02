package br.com.fitnesspro.workout.usecase.workout

import br.com.fitnesspro.workout.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateWorkoutUseCase(private val workoutRepository: WorkoutRepository) {

    suspend operator fun invoke(workoutId: String) = withContext(IO) {
        workoutRepository.runInTransaction {
            workoutRepository.inactivateWorkout(workoutId)
        }
    }
}