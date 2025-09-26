package br.com.fitnesspro.tuple

import java.time.Instant

data class ExecutionDurationTuple(
    val executionStartTime: Instant,
    val executionEndTime: Instant?
)