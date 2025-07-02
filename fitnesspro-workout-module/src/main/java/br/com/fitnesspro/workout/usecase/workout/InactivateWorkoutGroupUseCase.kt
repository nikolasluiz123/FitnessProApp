package br.com.fitnesspro.workout.usecase.workout

import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository

class InactivateWorkoutGroupUseCase(
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(workoutGroupId: String) {
        workoutGroupRepository.runInTransaction {
            val inactivateWorkoutGroupRemoteSuccess = workoutGroupRepository.inactivateWorkoutGroup(workoutGroupId)
            exerciseRepository.inactivateExercisesFromWorkoutGroup(workoutGroupId, inactivateWorkoutGroupRemoteSuccess)
        }
    }

}