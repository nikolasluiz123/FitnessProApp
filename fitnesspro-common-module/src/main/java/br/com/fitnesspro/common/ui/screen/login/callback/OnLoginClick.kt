package br.com.fitnesspro.common.ui.screen.login.callback

fun interface OnLoginClick {
    fun onExecute(onSuccess: () -> Unit)
}