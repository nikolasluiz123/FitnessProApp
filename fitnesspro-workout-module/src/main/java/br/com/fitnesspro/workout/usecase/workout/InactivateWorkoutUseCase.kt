package br.com.fitnesspro.workout.usecase.workout

import android.content.Context
import br.com.fitnesspro.core.exceptions.NoLoggingException
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository

class InactivateWorkoutUseCase(
    private val context: Context,
    private val workoutRepository: WorkoutRepository,
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(workoutId: String) {
        val existsVideoExerciseTransmitted = videoRepository.getExistsVideoExerciseTransmitted(workoutId = workoutId)

        if (!context.isNetworkAvailable() && existsVideoExerciseTransmitted) {
            throw NoLoggingException(context.getString(R.string.network_required_inactivate_workout_message))
        }

        workoutRepository.runInTransaction {
            workoutRepository.inactivateWorkout(workoutId)
        }
    }
}