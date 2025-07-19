package br.com.fitnesspro.tuple

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class ExerciseExecutionGroupedTuple(
    val id: String? = null,
    val duration: Long? = null,
    val durationUnit: ChronoUnit? = null,
    val repetitions: Int? = null,
    val actualSet: Int? = null,
    val rest: Long? = null,
    val restUnit: ChronoUnit? = null,
    val weight: Double? = null,
    val date: LocalDateTime? = null,
    val groupDate: LocalDate? = null,
    val sortOrder: Int = 0
) {
    val isGroup: Boolean
        get() = sortOrder == 0
}