package br.com.fitnesspro.workout.usecase

import android.content.Context
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository

class SaveExerciseUseCase(
    private val context: Context,
    private val workoutRepository: WorkoutRepository,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(toExercise: TOExercise): List<FieldValidationError<EnumValidatedExerciseFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedExerciseFields>>()
        validateWorkoutGroup(toExercise)?.let(validationResults::add)


        return validationResults
    }

    private fun validateWorkoutGroup(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        return when {
            toExercise.workoutGroupId.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EnumValidatedExerciseFields.EXERCISE_GROUP.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedExerciseFields.EXERCISE_GROUP,
                    message = message
                )
            }

            else -> null
        }
    }
}