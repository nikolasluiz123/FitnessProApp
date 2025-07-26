package br.com.fitnesspro.local.data.access.dao.filters

import java.time.LocalDate

data class SchedulerReportFilter(
    val personId: String,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null
)