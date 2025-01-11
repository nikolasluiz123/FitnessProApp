package br.com.fitnesspro.scheduler.ui.screen.scheduler.callback

import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType

fun interface OnSaveCompromiseClick {
    fun onExecute(onSaved: (EnumSchedulerType) -> Unit)
}