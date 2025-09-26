package br.com.fitnesspro.tuple.reports.evolution

import java.time.LocalDate

data class ResumeRegisterEvolutionWorkoutTuple(
    val dateStart: LocalDate,
    val dateEnd: LocalDate,
    val professionalPersonName: String
)