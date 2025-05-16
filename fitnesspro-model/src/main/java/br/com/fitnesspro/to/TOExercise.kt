package br.com.fitnesspro.to

data class TOExercise(
    override var id: String? = null,
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var observation: String? = null,
    var workoutGroupId: String? = null,
    var active: Boolean = true
): BaseTO