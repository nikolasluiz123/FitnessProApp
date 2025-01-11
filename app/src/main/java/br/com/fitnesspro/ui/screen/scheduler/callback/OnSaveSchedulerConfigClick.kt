package br.com.fitnesspro.ui.screen.scheduler.callback

fun interface OnSaveSchedulerConfigClick {
    fun onExecute(onSaved: () -> Unit)
}