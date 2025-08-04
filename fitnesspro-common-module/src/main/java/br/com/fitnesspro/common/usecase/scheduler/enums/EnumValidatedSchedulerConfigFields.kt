package br.com.fitnesspro.common.usecase.scheduler.enums

import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.enums.IEnumFieldValidation


enum class EnumValidatedSchedulerConfigFields(override val labelResId: Int, override val maxLength: Int = 0) : IEnumFieldValidation {
    MIN_SCHEDULE_DENSITY(R.string.enum_scheduler_config_min_event_density),
    MAX_SCHEDULE_DENSITY(R.string.enum_scheduler_config_max_event_density),
    NOTIFICATION_ANTECEDENCE_TIME(R.string.enum_scheduler_config_notification_antecedence_time)
}