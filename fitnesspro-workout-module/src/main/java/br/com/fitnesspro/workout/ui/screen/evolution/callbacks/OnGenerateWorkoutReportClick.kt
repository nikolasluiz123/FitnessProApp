package br.com.fitnesspro.workout.ui.screen.evolution.callbacks

fun interface OnGenerateWorkoutReportClick {
    fun onExecute(onSuccess: (filePath: String) -> Unit)
}