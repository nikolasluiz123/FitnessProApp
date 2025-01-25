package br.com.fitnesspro.common.usecase.academy

import br.com.fitnesspro.common.R


enum class EnumValidatedAcademyFields(val labelResId: Int) {
    ACADEMY(R.string.enum_validated_academy_fields_academy),
    TIME_START(R.string.enum_validated_academy_fields_time_start),
    TIME_END(R.string.enum_validated_academy_fields_time_end),
    DAY_OF_WEEK(R.string.enum_validated_academy_fields_day_of_week)
}