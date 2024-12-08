package br.com.fitnesspro.ui.screen.registeruser.callback

fun interface OnSaveUserClick {

    fun onExecute(onSaved: () -> Unit)
}