package br.com.fitnesspro.workout.usecase

import android.content.Context
import br.com.fitnesspro.core.extensions.toMillis
import br.com.fitnesspro.core.validation.FieldValidationTypedError
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.usecase.EnumValidatedExerciseFields.EXERCISE
import br.com.fitnesspro.workout.usecase.EnumValidatedExerciseFields.EXERCISE_GROUP
import br.com.fitnesspro.workout.usecase.EnumValidatedExerciseFields.OBSERVATION
import java.time.temporal.ChronoUnit

class SaveExerciseUseCase(
    private val context: Context,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val exerciseRepository: ExerciseRepository

) {
    suspend operator fun invoke(toExercise: TOExercise): List<FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>> {
        val validationResults = mutableListOf<FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>>()
        validateWorkoutGroup(toExercise)?.let(validationResults::add)
        validateExercise(toExercise).let(validationResults::addAll)

        if (validationResults.isEmpty()) {
            exerciseRepository.saveExercise(toExercise)
        }

        return validationResults
    }

    private suspend fun validateWorkoutGroup(toExercise: TOExercise): FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>? {
        val existentWorkoutGroup = toExercise.workoutGroupName?.let {
            workoutGroupRepository.findWorkoutGroupByName(
                workoutId = toExercise.workoutId!!,
                name = it
            )
        }

        return when {
            toExercise.workoutGroupName.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EXERCISE_GROUP.labelResId)
                )

                FieldValidationTypedError(
                    type = null,
                    field = EXERCISE_GROUP,
                    message = message
                )
            }

            toExercise.workoutGroupName!!.length > EXERCISE_GROUP.maxLength -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                    context.getString(EXERCISE_GROUP.labelResId),
                    EXERCISE_GROUP.maxLength
                )

                FieldValidationTypedError(
                    type = null,
                    field = EXERCISE_GROUP,
                    message = message
                )
            }

            existentWorkoutGroup == null -> {
//                val message = context.getString(
//                    R.string.validation_msg_new_workout_group_creation,
//                    toExercise.workoutGroupName
//                )
//
//                FieldValidationTypedError(
//                    type = EnumValidatedExerciseType.NEW_WORKOUT_GROUP,
//                    field = null,
//                    message = message
//                )
                null
            }

            toExercise.workoutGroupId.isNullOrEmpty() -> {
                toExercise.workoutGroupId = existentWorkoutGroup.id
                toExercise.workoutGroupName = existentWorkoutGroup.name

                null
            }

            else -> null
        }
    }

    private suspend fun validateExercise(toExercise: TOExercise): List<FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>> {
        val validationResults = mutableListOf<FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>>()
        validateExerciseName(toExercise)?.let(validationResults::add)
        validateExerciseRest(toExercise)?.let(validationResults::add)
        validateExerciseDuration(toExercise)?.let(validationResults::add)
        validateExerciseObservation(toExercise)?.let(validationResults::add)

        return validationResults
    }

    private suspend fun validateExerciseName(toExercise: TOExercise): FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>? {
        val existentExercisePreDefinition = toExercise.name?.let {
            exercisePreDefinitionRepository.findExercisePreDefinitionByName(it)
        }

        return when {
            toExercise.name.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EXERCISE.labelResId)
                )

                FieldValidationTypedError(
                    type = null,
                    field = EXERCISE,
                    message = message
                )
            }

            toExercise.name!!.length > EXERCISE.maxLength -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                    context.getString(EXERCISE.labelResId),
                    EXERCISE.maxLength
                )

                FieldValidationTypedError(
                    type = null,
                    field = EXERCISE,
                    message = message
                )
            }

            existentExercisePreDefinition == null -> {
//                val message = context.getString(
//                    R.string.validation_msg_new_exercise_creation,
//                    toExercise.name
//                )
//
//                FieldValidationTypedError(
//                    type = EnumValidatedExerciseType.NEW_EXERCISE_PRE_DEFINITION,
//                    field = null,
//                    message = message
//                )
                null
            }

            else -> {
                toExercise.sets = existentExercisePreDefinition.sets
                toExercise.repetitions = existentExercisePreDefinition.repetitions
                toExercise.rest = existentExercisePreDefinition.rest
                toExercise.duration = existentExercisePreDefinition.duration
                toExercise.unitDuration = ChronoUnit.SECONDS
                toExercise.unitRest = ChronoUnit.SECONDS

                null
            }
        }
    }

    private fun validateExerciseRest(toExercise: TOExercise): FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>? {
        val validationError = when {
            (toExercise.rest != null) != (toExercise.unitRest != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_rest_or_unit)

                FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>(
                    type = null,
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null && toExercise.rest != null) {
            toExercise.rest = toExercise.rest!!.toMillis(toExercise.unitRest!!)
        }

        return validationError
    }

    private fun validateExerciseDuration(toExercise: TOExercise): FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>? {
        val validationError = when {
            (toExercise.duration != null) != (toExercise.unitDuration != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_duration_or_unit)

                FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>(
                    type = null,
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null && toExercise.duration != null) {
            toExercise.duration = toExercise.duration!!.toMillis(toExercise.unitDuration!!)
        }

        return validationError
    }

    private fun validateExerciseObservation(toExercise: TOExercise): FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>? {
        return when {
            !toExercise.observation.isNullOrEmpty() && toExercise.observation!!.length > OBSERVATION.maxLength -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                    context.getString(OBSERVATION.labelResId),
                    OBSERVATION.maxLength
                )

                FieldValidationTypedError(
                    type = null,
                    field = OBSERVATION,
                    message = message
                )
            }

            else -> null
        }
    }
}