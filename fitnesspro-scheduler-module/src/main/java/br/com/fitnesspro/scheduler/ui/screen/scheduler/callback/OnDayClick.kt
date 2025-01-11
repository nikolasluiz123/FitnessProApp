package br.com.fitnesspro.scheduler.ui.screen.scheduler.callback

import br.com.fitnesspro.scheduler.ui.navigation.SchedulerDetailsScreenArgs

fun interface OnDayClick {
    fun onExecute(args: SchedulerDetailsScreenArgs)
}