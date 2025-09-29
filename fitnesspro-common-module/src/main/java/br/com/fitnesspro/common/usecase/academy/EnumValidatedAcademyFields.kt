package br.com.fitnesspro.common.usecase.academy

import br.com.android.ui.compose.components.fields.validation.interfaces.IEnumFieldValidation
import br.com.fitnesspro.common.R


enum class EnumValidatedAcademyFields(override val labelResId: Int, override val maxLength: Int = 0) :
    IEnumFieldValidation {
    ACADEMY(R.string.enum_validated_academy_fields_academy),
    TIME_START(R.string.enum_validated_academy_fields_time_start),
    TIME_END(R.string.enum_validated_academy_fields_time_end),
    DAY_OF_WEEK(R.string.enum_validated_academy_fields_day_of_week)
}