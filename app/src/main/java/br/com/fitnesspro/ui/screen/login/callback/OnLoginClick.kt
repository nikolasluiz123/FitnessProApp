package br.com.fitnesspro.ui.screen.login.callback

fun interface OnLoginClick {
    fun onExecute(onSuccess: () -> Unit)
}