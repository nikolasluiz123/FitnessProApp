package br.com.fitnesspro.to

data class TOVideoExerciseExecution(
    override var id: String? = null,
    var exerciseExecutionId: String? = null,
    var toVideo: TOVideo? = null,
    var active: Boolean = true
): BaseTO