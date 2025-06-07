package br.com.fitnesspro.workout.usecase.workout

import br.com.fitnesspro.workout.R


enum class EnumValidatedWorkoutGroupFields(val labelResId: Int, val maxLength: Int = 0) {
    GROUP_NAME(R.string.enum_workout_group_name, 255),
    DAY_WEEK(R.string.enum_workout_group_day_week, 0),
    EXERCISE_GROUP_ORDER(R.string.enum_workout_group_exercise_group_order, 0)
}