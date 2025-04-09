package br.com.fitnesspro.scheduler.ui.screen.compromisse.callbacks

import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType

fun interface OnSaveCompromiseClick {
    fun onExecute(onSaved: (EnumSchedulerType) -> Unit)
}