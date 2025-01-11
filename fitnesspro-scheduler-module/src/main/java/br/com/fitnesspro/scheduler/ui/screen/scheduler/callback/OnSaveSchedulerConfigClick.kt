package br.com.fitnesspro.scheduler.ui.screen.scheduler.callback

fun interface OnSaveSchedulerConfigClick {
    fun onExecute(onSaved: () -> Unit)
}