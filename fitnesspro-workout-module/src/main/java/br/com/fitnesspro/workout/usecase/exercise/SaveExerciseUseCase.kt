package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.toMillis
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseFields
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseFields.EXERCISE
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseFields.EXERCISE_GROUP
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseFields.EXERCISE_GROUP_ORDER
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseFields.EXERCISE_ORDER
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseFields.OBSERVATION
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SaveExerciseUseCase(
    private val context: Context,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val exerciseRepository: ExerciseRepository

) {
    suspend operator fun invoke(toExercise: TOExercise): List<FieldValidationError<EnumValidatedExerciseFields>> = withContext(IO) {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedExerciseFields>>()
        validationResults.addAll(validateWorkoutGroup(toExercise))
        validationResults.addAll(validateExercise(toExercise))

        if (validationResults.isEmpty()) {
            exerciseRepository.saveExercise(toExercise)
        }

        validationResults
    }

    private suspend fun validateWorkoutGroup(toExercise: TOExercise): List<FieldValidationError<EnumValidatedExerciseFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedExerciseFields>?>()
        validationResults.add(validateWorkoutGroupName(toExercise))
        validationResults.add(validateWorkoutGroupOrder(toExercise))

        if (validationResults.isEmpty()) {
            validateWorkoutGroupId(toExercise)
        }

        return validationResults.filterNotNull()
    }

    private fun validateWorkoutGroupName(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        return when {
            toExercise.workoutGroupName.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EXERCISE_GROUP.labelResId)
                )

                FieldValidationError(
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

                FieldValidationError(
                    field = EXERCISE_GROUP,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateWorkoutGroupOrder(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        return when {
            toExercise.groupOrder == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EXERCISE_GROUP_ORDER.labelResId)
                )

                FieldValidationError(
                    field = EXERCISE_GROUP_ORDER,
                    message = message
                )
            }

            toExercise.groupOrder!! <= 0 -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_invalid_field,
                    context.getString(EXERCISE_GROUP_ORDER.labelResId),
                )

                FieldValidationError(
                    field = EXERCISE_GROUP_ORDER,
                    message = message
                )
            }

            else -> null
        }
    }

    private suspend fun validateWorkoutGroupId(toExercise: TOExercise) {
        val existentWorkoutGroup = toExercise.workoutGroupName?.let {
            workoutGroupRepository.findWorkoutGroupByName(
                workoutId = toExercise.workoutId!!,
                name = it
            )
        }

        if (toExercise.workoutGroupId.isNullOrEmpty() && existentWorkoutGroup != null) {
            toExercise.workoutGroupId = existentWorkoutGroup.id
            toExercise.workoutGroupName = existentWorkoutGroup.name
        }
    }

    private suspend fun validateExercise(toExercise: TOExercise): List<FieldValidationError<EnumValidatedExerciseFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedExerciseFields>>()
        validateExerciseName(toExercise)?.let(validationResults::add)
        validateExerciseOrder(toExercise)?.let(validationResults::add)
        validateExerciseRest(toExercise)?.let(validationResults::add)
        validateExerciseDuration(toExercise)?.let(validationResults::add)
        validateExerciseObservation(toExercise)?.let(validationResults::add)

        if (validationResults.isEmpty()) {
            validateExercisePreDefinition(toExercise)
        }

        return validationResults
    }

    private fun validateExerciseName(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        return when {
            toExercise.name.isNullOrEmpty() -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EXERCISE.labelResId)
                )

                FieldValidationError(
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

                FieldValidationError(
                    field = EXERCISE,
                    message = message
                )
            }

            else -> null
        }
    }

    private suspend fun validateExercisePreDefinition(toExercise: TOExercise) {
        val existentExercisePreDefinition = toExercise.name?.let {
            exercisePreDefinitionRepository.findExercisePreDefinitionByName(it)
        }

        existentExercisePreDefinition?.let {
            toExercise.sets = it.sets
            toExercise.repetitions = it.repetitions
            toExercise.rest = it.rest
            toExercise.duration = it.duration
            toExercise.unitDuration = it.duration?.bestChronoUnit()
            toExercise.unitRest = it.rest?.bestChronoUnit()
        }
    }

    private fun validateExerciseOrder(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        return when {
            toExercise.exerciseOrder == null -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_required_field,
                    context.getString(EXERCISE_ORDER.labelResId)
                )

                FieldValidationError(
                    field = EXERCISE_ORDER,
                    message = message
                )
            }

            toExercise.exerciseOrder!! <= 0 -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_invalid_field,
                    context.getString(EXERCISE_ORDER.labelResId),
                )

                FieldValidationError(
                    field = EXERCISE_ORDER,
                    message = message
                )
            }

            else -> null
        }
    }

    private fun validateExerciseRest(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        val validationError = when {
            (toExercise.rest != null) != (toExercise.unitRest != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_rest_or_unit)

                FieldValidationError<EnumValidatedExerciseFields>(
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null &&
            toExercise.rest != null &&
            toExercise.rest?.bestChronoUnit() != toExercise.unitRest) {

            toExercise.rest = toExercise.rest!!.toMillis(toExercise.unitRest!!)
        }

        return validationError
    }

    private fun validateExerciseDuration(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        val validationError = when {
            (toExercise.duration != null) != (toExercise.unitDuration != null) -> {
                val message = context.getString(R.string.validation_msg_invalid_duration_or_unit)

                FieldValidationError<EnumValidatedExerciseFields>(
                    field = null,
                    message = message
                )
            }

            else -> null
        }

        if (validationError == null &&
            toExercise.duration != null &&
            toExercise.duration?.bestChronoUnit() != toExercise.unitDuration) {

            toExercise.duration = toExercise.duration!!.toMillis(toExercise.unitDuration!!)
        }

        return validationError
    }

    private fun validateExerciseObservation(toExercise: TOExercise): FieldValidationError<EnumValidatedExerciseFields>? {
        return when {
            !toExercise.observation.isNullOrEmpty() && toExercise.observation!!.length > OBSERVATION.maxLength -> {
                val message = context.getString(
                    br.com.fitnesspro.common.R.string.validation_msg_field_with_max_length,
                    context.getString(OBSERVATION.labelResId),
                    OBSERVATION.maxLength
                )

                FieldValidationError(
                    field = OBSERVATION,
                    message = message
                )
            }

            else -> null
        }
    }
}