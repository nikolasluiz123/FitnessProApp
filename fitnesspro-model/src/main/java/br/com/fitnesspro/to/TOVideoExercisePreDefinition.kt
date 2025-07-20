package br.com.fitnesspro.to

data class TOVideoExercisePreDefinition(
    override var id: String? = null,
    var exercisePreDefinitionId: String? = null,
    var toVideo: TOVideo? = null,
): BaseTO