package br.com.fitnesspro.workout.usecase.workout.enums

import br.com.fitnesspro.core.enums.IEnumFieldValidation
import br.com.fitnesspro.workout.R

enum class EnumValidatedWorkoutGroupFields(override val labelResId: Int, override val maxLength: Int = 0) : IEnumFieldValidation {
    GROUP_NAME(R.string.enum_workout_group_name, 255),
    DAY_WEEK(R.string.enum_workout_group_day_week),
    EXERCISE_GROUP_ORDER(R.string.enum_workout_group_exercise_group_order)
}