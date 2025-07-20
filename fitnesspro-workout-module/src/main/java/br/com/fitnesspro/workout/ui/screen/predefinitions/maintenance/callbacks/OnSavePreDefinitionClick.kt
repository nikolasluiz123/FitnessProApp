package br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.callbacks

fun interface OnSavePreDefinitionClick {
    fun onExecute(onSuccess: () -> Unit)
}