package br.com.fitnesspro.to

data class TOWorkoutGroupPreDefinition(
    override var id: String? = null,
    var name: String? = null,
    var personalTrainerPersonId: String? = null,
    var active: Boolean = true
): BaseTO