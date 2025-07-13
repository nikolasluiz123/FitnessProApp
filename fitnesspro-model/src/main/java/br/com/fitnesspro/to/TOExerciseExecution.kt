package br.com.fitnesspro.to

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class TOExerciseExecution(
    override var id: String? = null,
    var exerciseId: String? = null,
    var duration: Long? = null,
    var durationUnit: ChronoUnit? = null,
    var repetitions: Int? = null,
    var set: Int? = null,
    var rest: Long? = null,
    var restUnit: ChronoUnit? = null,
    var weight: Double? = null,
    var date: LocalDateTime? = null,
    var active: Boolean = true
): BaseTO