package br.com.fitnesspro.workout.ui.screen.exercice.callbacks

fun interface OnSaveExerciseClick {
    fun onExecute(onSaved: () -> Unit)
}