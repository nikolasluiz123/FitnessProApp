package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExercisePreDefinitionFields
import br.com.fitnesspro.workout.usecase.workout.enums.EnumValidatedWorkoutGroupFields

class EditWorkoutGroupPreDefinitionUseCase(
    private val context: Context,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository
) {
    suspend operator fun invoke(toWorkoutGroup: TOWorkoutGroupPreDefinition): List<FieldValidationError<EnumValidatedExercisePreDefinitionFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedExercisePreDefinitionFields>>()
        validateName(toWorkoutGroup)?.let(validationResults::add)

        if (validationResults.isEmpty()) {
            exercisePreDefinitionRepository.saveWorkoutGroupPreDefinition(toWorkoutGroup)
        }

        return validationResults
    }

    private fun validateName(toWorkoutGroup: TOWorkoutGroupPreDefinition): FieldValidationError<EnumValidatedExercisePreDefinitionFields>? {
        return when {
            toWorkoutGroup.name.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedWorkoutGroupFields.GROUP_NAME.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedExercisePreDefinitionFields.GROUP,
                    message = message
                )
            }

            toWorkoutGroup.name!!.length > EnumValidatedExercisePreDefinitionFields.GROUP.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(EnumValidatedWorkoutGroupFields.GROUP_NAME.labelResId),
                    EnumValidatedWorkoutGroupFields.GROUP_NAME.maxLength
                )

                FieldValidationError(
                    field = EnumValidatedExercisePreDefinitionFields.GROUP,
                    message = message
                )
            }

            else -> null
        }
    }
}