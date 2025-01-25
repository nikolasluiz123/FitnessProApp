package br.com.fitnesspro.scheduler.ui.screen.scheduler.callback

fun interface OnInactivateCompromiseClick {
    fun onExecute(onSuccess: () -> Unit)
}