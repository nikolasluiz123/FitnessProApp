package br.com.fitnesspro.tuple.reports.evolution

import java.time.DayOfWeek

data class WorkoutGroupInfosTuple(
    val dayWeek: DayOfWeek,
    val name: String
)