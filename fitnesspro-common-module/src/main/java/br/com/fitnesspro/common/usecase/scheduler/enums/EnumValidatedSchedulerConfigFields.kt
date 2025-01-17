package br.com.fitnesspro.common.usecase.scheduler.enums

import br.com.fitnesspro.common.R


enum class EnumValidatedSchedulerConfigFields(val labelResId: Int) {
    MIN_SCHEDULE_DENSITY(R.string.enum_scheduler_config_min_event_density),
    MAX_SCHEDULE_DENSITY(R.string.enum_scheduler_config_max_event_density),
    START_WORK_TIME(R.string.enum_scheduler_config_start_work_time),
    END_WORK_TIME(R.string.enum_scheduler_config_end_work_time),
    START_BREAK_TIME(R.string.enum_scheduler_config_start_break_time),
    END_BREAK_TIME(R.string.enum_scheduler_config_end_break_time)
}