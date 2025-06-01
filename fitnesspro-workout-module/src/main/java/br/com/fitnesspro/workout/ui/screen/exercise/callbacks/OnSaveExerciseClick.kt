package br.com.fitnesspro.workout.ui.screen.exercise.callbacks

fun interface OnSaveExerciseClick {
    fun onExecute(onSaved: () -> Unit)
}