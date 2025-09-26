package br.com.fitnesspro.tuple.reports.evolution

data class ExerciseInfosTuple(
    val id: String,
    val name: String,
    val repetitions: Int?,
    val sets: Int?,
    val rest: Long?,
    val duration: Long?
)