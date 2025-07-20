package br.com.fitnesspro.tuple

data class PreDefinitionTuple(
    var id: String? = null,
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var workoutGroupPreDefinitionName: String? = null,
    var workoutGroupPreDefinitionId: String? = null,
    val sortOrder: Int = 0
) {
    val isGroup: Boolean
        get() = sortOrder == 0
}
