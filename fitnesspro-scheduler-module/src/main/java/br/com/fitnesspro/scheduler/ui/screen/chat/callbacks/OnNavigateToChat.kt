package br.com.fitnesspro.scheduler.ui.screen.chat.callbacks

import br.com.fitnesspro.scheduler.ui.navigation.ChatArgs

fun interface OnNavigateToChat {
    fun onExecute(chatArgs: ChatArgs)
}