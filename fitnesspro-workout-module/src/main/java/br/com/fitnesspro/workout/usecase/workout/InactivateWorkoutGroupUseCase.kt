package br.com.fitnesspro.workout.usecase.workout

import android.content.Context
import br.com.fitnesspro.core.exceptions.NoLoggingException
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository

class InactivateWorkoutGroupUseCase(
    private val context: Context,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(workoutGroupId: String) {
        if (!context.isNetworkAvailable()) {
            throw NoLoggingException(context.getString(R.string.network_required_inactivate_workout_group_message))
        }

        workoutGroupRepository.runInTransaction {
            val inactivateWorkoutGroupRemoteSuccess = workoutGroupRepository.inactivateWorkoutGroup(workoutGroupId)
            exerciseRepository.inactivateExercisesFromWorkoutGroup(workoutGroupId, inactivateWorkoutGroupRemoteSuccess)
        }
    }

}