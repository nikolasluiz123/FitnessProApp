package br.com.fitnesspro.scheduler.usecase.report.enums

import br.com.fitnesspro.scheduler.R

enum class EnumValidatedSchedulerReportFields(val labelResId: Int, val maxLength: Int) {
    NAME(R.string.enum_validated_scheduler_report_fields_name, 256),
}