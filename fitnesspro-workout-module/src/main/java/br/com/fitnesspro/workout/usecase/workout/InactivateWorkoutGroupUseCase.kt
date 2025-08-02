package br.com.fitnesspro.workout.usecase.workout

import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateWorkoutGroupUseCase(
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val exerciseRepository: ExerciseRepository,
) {
    suspend operator fun invoke(workoutGroupId: String) = withContext(IO) {
        workoutGroupRepository.runInTransaction {
            workoutGroupRepository.inactivateWorkoutGroup(workoutGroupId)

            exerciseRepository.inactivateExercisesFromWorkoutGroupLocally(
                listWorkoutGroupId = listOf(workoutGroupId)
            )
        }
    }
}