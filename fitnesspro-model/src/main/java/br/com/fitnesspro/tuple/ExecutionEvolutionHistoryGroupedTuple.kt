package br.com.fitnesspro.tuple

import java.time.LocalDate

data class ExecutionEvolutionHistoryGroupedTuple(
    val exerciseId: String? = null,
    val executionDate: LocalDate? = null,
    val exerciseName: String? = null,
    val sortOrder: Int = 0
) {
    val isGroup: Boolean
        get() = sortOrder == 0
}