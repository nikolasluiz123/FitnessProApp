package br.com.fitnesspro.workout.usecase.workout

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.usecase.workout.enums.EnumValidatedWorkoutGroupFields
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class EditWorkoutGroupUseCase(
    private val context: Context,
    private val workoutGroupRepository: WorkoutGroupRepository
) {
    suspend operator fun invoke(toWorkoutGroup: TOWorkoutGroup): List<FieldValidationError<EnumValidatedWorkoutGroupFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedWorkoutGroupFields>>()
        validateName(toWorkoutGroup)?.let(validationResults::add)
        validateDayWeek(toWorkoutGroup)?.let(validationResults::add)
        validateWorkoutGroupOrder(toWorkoutGroup)?.let(validationResults::add)

        if (validationResults.isEmpty()) {
            withContext(IO) {
                workoutGroupRepository.saveWorkoutGroup(toWorkoutGroup)
            }
        }

        return validationResults
    }

    private fun validateName(toWorkoutGroup: TOWorkoutGroup): FieldValidationError<EnumValidatedWorkoutGroupFields>? {
        return when {
            toWorkoutGroup.name.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedWorkoutGroupFields.GROUP_NAME.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedWorkoutGroupFields.GROUP_NAME,
                    message = message
                )
            }

            toWorkoutGroup.name!!.length > EnumValidatedWorkoutGroupFields.GROUP_NAME.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(EnumValidatedWorkoutGroupFields.GROUP_NAME.labelResId),
                    EnumValidatedWorkoutGroupFields.GROUP_NAME.maxLength
                )

                FieldValidationError(
                    field = EnumValidatedWorkoutGroupFields.GROUP_NAME,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateDayWeek(toWorkoutGroup: TOWorkoutGroup): FieldValidationError<EnumValidatedWorkoutGroupFields>? {
        return when {
            toWorkoutGroup.dayWeek == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedWorkoutGroupFields.DAY_WEEK.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedWorkoutGroupFields.DAY_WEEK,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateWorkoutGroupOrder(toWorkoutGroup: TOWorkoutGroup): FieldValidationError<EnumValidatedWorkoutGroupFields>? {
        return when {
            toWorkoutGroup.order == null -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EnumValidatedWorkoutGroupFields.EXERCISE_GROUP_ORDER.labelResId)
                )

                FieldValidationError(
                    field = EnumValidatedWorkoutGroupFields.EXERCISE_GROUP_ORDER,
                    message = message
                )
            }

            toWorkoutGroup.order!! <= 0 -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(EnumValidatedWorkoutGroupFields.EXERCISE_GROUP_ORDER.labelResId),
                )

                FieldValidationError(
                    field = EnumValidatedWorkoutGroupFields.EXERCISE_GROUP_ORDER,
                    message = message
                )
            }

            else -> null
        }
    }
}