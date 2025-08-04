package br.com.fitnesspro.scheduler.usecase.report.enums

import br.com.fitnesspro.core.enums.IEnumFieldValidation
import br.com.fitnesspro.scheduler.R

enum class EnumValidatedSchedulerReportFields(override val labelResId: Int, override val maxLength: Int = 0) : IEnumFieldValidation {
    NAME(R.string.enum_validated_scheduler_report_fields_name, 256),
}