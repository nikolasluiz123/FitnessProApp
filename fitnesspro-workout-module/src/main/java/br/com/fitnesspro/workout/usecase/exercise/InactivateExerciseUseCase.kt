package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import br.com.fitnesspro.core.exceptions.NoLoggingException
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.VideoRepository

class InactivateExerciseUseCase(
    private val context: Context,
    private val exerciseRepository: ExerciseRepository,
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(exerciseId: String) {
        val existsVideoExerciseTransmitted = videoRepository.getExistsVideoExerciseTransmitted(exerciseId = exerciseId)

        if (!context.isNetworkAvailable() && existsVideoExerciseTransmitted) {
            throw NoLoggingException(context.getString(R.string.network_required_inactivate_exercise_message))
        }

        exerciseRepository.runInTransaction {
            exerciseRepository.inactivateExercise(exerciseId)
        }
    }

}