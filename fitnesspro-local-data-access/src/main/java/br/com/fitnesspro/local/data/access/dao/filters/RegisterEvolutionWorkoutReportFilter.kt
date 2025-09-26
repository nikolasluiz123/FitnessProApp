package br.com.fitnesspro.local.data.access.dao.filters

import java.time.LocalDate

data class RegisterEvolutionWorkoutReportFilter(
    val workoutId: String,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null
)