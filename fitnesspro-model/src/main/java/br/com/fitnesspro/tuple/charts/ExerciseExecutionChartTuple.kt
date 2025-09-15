package br.com.fitnesspro.tuple.charts

import java.time.LocalDate

data class ExerciseExecutionChartTuple(
    val date: LocalDate,
    val weight: Double?,
    val reps: Int?,
    val rest: Long?,
    val duration: Long?,
)