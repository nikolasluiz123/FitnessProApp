package br.com.fitnesspro.usecase.scheduler.enums

import br.com.fitnesspro.R

enum class EnumValidatedCompromiseFields(val labelResId: Int, val maxLength: Int = 0) {
    MEMBER(R.string.enum_compromise_member, 0),
    PROFESSIONAL(R.string.enum_compromise_professional, 0),
    DATE_START(R.string.enum_compromise_date_start, 0),
    DATE_END(R.string.enum_compromise_date_end, 0),
    HOUR_START(R.string.enum_compromise_hour_start, 0),
    HOUR_END(R.string.enum_compromise_hour_end, 0),
    OBSERVATION(R.string.enum_compromise_observation, 4096),
    DAY_OF_WEEKS(R.string.enum_compromise_day_of_weeks, 0)
}