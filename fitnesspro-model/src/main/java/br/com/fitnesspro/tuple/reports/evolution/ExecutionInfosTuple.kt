package br.com.fitnesspro.tuple.reports.evolution

import java.time.Instant

data class ExecutionInfosTuple(
    val executionStartTime: Instant,
    val executionEndTime: Instant?,
    val actualSet: Int,
    val weight: Double?,
    val repetitions: Int?,
    val duration: Long?
)