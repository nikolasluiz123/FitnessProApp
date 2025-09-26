package br.com.fitnesspro.workout.usecase.reports

import br.com.fitnesspro.core.enums.IEnumFieldValidation
import br.com.fitnesspro.workout.R

enum class EnumValidatedRegisterEvolutionReportFields(
    override val labelResId: Int,
    override val maxLength: Int = 0
) : IEnumFieldValidation {
    WORKOUT(R.string.enum_validated_register_evolution_report_fields_workout, 0),
    NAME(R.string.enum_validated_register_evolution_report_fields_name, 256),
    DATE_START(R.string.enum_validated_register_evolution_report_fields_date_start, 0),
    DATE_END(R.string.enum_validated_register_evolution_report_fields_date_end, 0),

}