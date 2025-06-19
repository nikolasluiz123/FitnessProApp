package br.com.fitnesspro.scheduler.reports

import java.time.LocalDate

data class SchedulerFilter(
    val personId: String,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null
)