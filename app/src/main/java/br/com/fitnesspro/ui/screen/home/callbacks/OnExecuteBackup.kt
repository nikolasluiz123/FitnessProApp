package br.com.fitnesspro.ui.screen.home.callbacks

fun interface OnExecuteBackup {
    fun onExecute(onSuccess: () -> Unit)
}