package br.com.fitnesspro.scheduler.usecase.report.enums

import br.com.android.ui.compose.components.fields.validation.interfaces.IEnumFieldValidation
import br.com.fitnesspro.scheduler.R

enum class EnumValidatedSchedulerReportFields(override val labelResId: Int, override val maxLength: Int = 0) :
    IEnumFieldValidation {
    NAME(R.string.enum_validated_scheduler_report_fields_name, 256),
}