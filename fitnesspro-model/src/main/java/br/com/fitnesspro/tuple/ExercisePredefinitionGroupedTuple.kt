package br.com.fitnesspro.tuple

import java.time.temporal.ChronoUnit

data class ExercisePredefinitionGroupedTuple(
    val id: String?? = null,
    val name: String? = null,
    val sets: Int? = null,
    val reps: Int? = null,
    val rest: Long? = null,
    val restUnit: ChronoUnit? = null,
    val duration: Long? = null,
    val durationUnit: ChronoUnit? = null,
    val groupId: String? = null,
    val groupName: String? = null,
    val isGroupHeader: Int
) {
    val isGroup: Boolean
        get() = isGroupHeader == 1
}