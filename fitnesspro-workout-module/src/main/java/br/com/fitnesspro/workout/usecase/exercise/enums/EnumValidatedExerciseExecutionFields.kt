package br.com.fitnesspro.workout.usecase.exercise.enums

import br.com.fitnesspro.workout.R

enum class EnumValidatedExerciseExecutionFields(val labelResId: Int) {
    REST(R.string.enum_exercise_rest),
    UNIT_REST(R.string.enum_exercise_unit_rest),
    DURATION(R.string.enum_exercise_duration),
    UNIT_DURATION(R.string.enum_exercise_unit_duration),
}