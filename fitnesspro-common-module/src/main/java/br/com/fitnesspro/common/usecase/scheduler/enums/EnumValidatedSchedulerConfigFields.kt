package br.com.fitnesspro.common.usecase.scheduler.enums

import br.com.fitnesspro.common.R


enum class EnumValidatedSchedulerConfigFields(val labelResId: Int) {
    MIN_SCHEDULE_DENSITY(R.string.enum_scheduler_config_min_event_density),
    MAX_SCHEDULE_DENSITY(R.string.enum_scheduler_config_max_event_density),
}