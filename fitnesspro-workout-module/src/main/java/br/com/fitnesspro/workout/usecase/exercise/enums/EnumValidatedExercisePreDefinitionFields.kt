package br.com.fitnesspro.workout.usecase.exercise.enums

import br.com.fitnesspro.workout.R

enum class EnumValidatedExercisePreDefinitionFields(val labelResId: Int, val maxLength: Int = 0) {
    GROUP(R.string.enum_exercise_group, 255),
    EXERCISE(R.string.enum_exercise, 255),
    REST(R.string.enum_exercise_rest),
    UNIT_REST(R.string.enum_exercise_unit_rest),
    DURATION(R.string.enum_exercise_duration),
    UNIT_DURATION(R.string.enum_exercise_unit_duration),
}