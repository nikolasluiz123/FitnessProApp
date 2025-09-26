package br.com.fitnesspro.workout.usecase.reports

import br.com.fitnesspro.core.enums.IEnumFieldValidation
import br.com.fitnesspro.workout.R

enum class EnumValidatedRegisterEvolutionReportFields(override val labelResId: Int, override val maxLength: Int = 0) :
    IEnumFieldValidation {
    NAME(R.string.enum_validated_register_evolution_report_fields_name, 256),
}