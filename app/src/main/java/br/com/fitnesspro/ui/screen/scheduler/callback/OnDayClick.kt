package br.com.fitnesspro.ui.screen.scheduler.callback

import br.com.fitnesspro.ui.navigation.SchedulerDetailsScreenArgs

fun interface OnDayClick {
    fun onExecute(args: SchedulerDetailsScreenArgs)
}