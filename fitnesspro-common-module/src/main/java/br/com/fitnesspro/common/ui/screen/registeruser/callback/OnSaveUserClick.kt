package br.com.fitnesspro.common.ui.screen.registeruser.callback

fun interface OnSaveUserClick {

    fun onExecute(onSaved: () -> Unit)
}