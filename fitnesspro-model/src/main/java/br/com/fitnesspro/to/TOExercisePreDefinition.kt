package br.com.fitnesspro.to

data class TOExercisePreDefinition(
    override var id: String? = null,
    var name: String? = null,
    var duration: Long? = null,
    var repetitions: Int? = null,
    var sets: Int? = null,
    var rest: Long? = null,
    var personalTrainerPersonId: String? = null,
    var workoutGroupPreDefinitionId: String? = null,
    var active: Boolean = true
): BaseTO {

}