package br.com.fitnesspro.to

data class TOVideoExercise(
    override var id: String? = null,
    var exerciseId: String? = null,
    var toVideo: TOVideo? = null,
): BaseTO