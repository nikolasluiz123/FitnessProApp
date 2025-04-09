package br.com.fitnesspro.scheduler.ui.screen.compromisse.callbacks

fun interface OnScheduleConfirmClick {
    fun onExecute(onSuccess: () -> Unit)
}