package br.com.fitnesspro.scheduler.ui.screen.scheduler.callback

fun interface OnScheduleConfirmClick {
    fun onExecute(onSuccess: () -> Unit)
}