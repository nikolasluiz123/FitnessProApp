package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExercisePreDefinitionFields
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExercisePreDefinitionFields.GROUP
import java.io.File

class SaveGroupPreDefinitionUseCase(
    private val context: Context,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val saveExercisePreDefinitionUseCase: SaveExercisePreDefinitionUseCase
) {
    suspend operator fun invoke(
        toWorkoutGroupPreDefinition: TOWorkoutGroupPreDefinition,
        toExercisePreDefinition: TOExercisePreDefinition,
        videoFiles: List<File>
    ): List<FieldValidationError<EnumValidatedExercisePreDefinitionFields>> {
        val validations = mutableListOf<FieldValidationError<EnumValidatedExercisePreDefinitionFields>>()
        validateWorkoutGroupPreDefinition(toWorkoutGroupPreDefinition)?.let(validations::add)

        if (validations.isEmpty()) {
            exercisePreDefinitionRepository.runInTransaction {
                exercisePreDefinitionRepository.saveWorkoutGroupPreDefinition(toWorkoutGroupPreDefinition)
                toExercisePreDefinition.workoutGroupPreDefinitionId = toWorkoutGroupPreDefinition.id

                val validationsExercisePreDefinition = saveExercisePreDefinitionUseCase(
                    toExercisePreDefinition = toExercisePreDefinition,
                    videoFiles = videoFiles,
                    transactional = false
                )

                validations.addAll(validationsExercisePreDefinition)
            }
        }

        return validations
    }

    private fun validateWorkoutGroupPreDefinition(toWorkoutGroupPreDefinition: TOWorkoutGroupPreDefinition): FieldValidationError<EnumValidatedExercisePreDefinitionFields>? {
        return when {
            toWorkoutGroupPreDefinition.name.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(GROUP.labelResId)
                )

                FieldValidationError(
                    field = GROUP,
                    message = message
                )
            }

            toWorkoutGroupPreDefinition.name!!.length > GROUP.maxLength -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                    context.getString(GROUP.labelResId),
                    GROUP.maxLength
                )

                FieldValidationError(
                    field = GROUP,
                    message = message
                )
            }

            else -> null
        }
    }
}