package br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.callbacks

fun interface OnInactivateWorkoutGroupPreDefinition {
    fun onExecute(onSuccess: () -> Unit)
}