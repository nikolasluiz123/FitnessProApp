package br.com.fitnesspro.workout.usecase.exercise

import br.com.fitnesspro.workout.R


enum class EnumValidatedExerciseFields(val labelResId: Int, val maxLength: Int = 0) {
    EXERCISE_GROUP(R.string.enum_exercise_group, 255),
    EXERCISE(R.string.enum_exercise, 255),
    SETS(R.string.enum_exercise_sets, 0),
    REPETITIONS(R.string.enum_exercise_repetitions, 0),
    REST(R.string.enum_exercise_rest, 0),
    UNIT_REST(R.string.enum_exercise_unit_rest, 0),
    DURATION(R.string.enum_exercise_duration, 0),
    UNIT_DURATION(R.string.enum_exercise_unit_duration, 0),
    OBSERVATION(R.string.enum_exercise_observation, 4096),
}