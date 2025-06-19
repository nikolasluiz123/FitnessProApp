package br.com.fitnesspro.workout.ui.screen.exercise.callbacks

fun interface OnFinishVideoRecording {
    fun onExecute(onSuccess: () -> Unit)
}