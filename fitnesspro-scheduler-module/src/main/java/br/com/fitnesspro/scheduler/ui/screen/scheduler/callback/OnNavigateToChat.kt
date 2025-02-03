package br.com.fitnesspro.scheduler.ui.screen.scheduler.callback

import br.com.fitnesspro.scheduler.ui.navigation.ChatArgs

fun interface OnNavigateToChat {
    fun onExecute(chatArgs: ChatArgs)
}