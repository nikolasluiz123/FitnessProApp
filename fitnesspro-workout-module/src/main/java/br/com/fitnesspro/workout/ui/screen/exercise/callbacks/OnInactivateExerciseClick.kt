package br.com.fitnesspro.workout.ui.screen.exercise.callbacks

fun interface OnInactivateExerciseClick {
    fun onExecute(onSuccess: () -> Unit)
}