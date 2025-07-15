package br.com.fitnesspro.workout.ui.screen.evolution.callbacks

fun interface OnSaveExerciseExecution {
    fun onExecute(onSuccess: () -> Unit)
}