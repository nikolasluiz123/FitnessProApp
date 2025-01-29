package br.com.fitnesspro.common.ui.screen.login.callback

import br.com.fitnesspro.to.TOPerson

fun interface OnLoginWithGoogle {

    fun onExecute(onUserNotExistsLocal: (TOPerson) -> Unit, onSuccess: () -> Unit, onFailure: () -> Unit)

}