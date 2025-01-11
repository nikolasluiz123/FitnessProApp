package br.com.fitnesspro.ui.screen.scheduler.callback

import br.com.fitnesspro.usecase.scheduler.enums.EnumSchedulerType

fun interface OnSaveCompromiseClick {
    fun onExecute(onSaved: (EnumSchedulerType) -> Unit)
}