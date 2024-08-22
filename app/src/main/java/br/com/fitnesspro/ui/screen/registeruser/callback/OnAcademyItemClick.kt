package br.com.fitnesspro.ui.screen.registeruser.callback

import br.com.fitnesspro.ui.navigation.RegisterAcademyScreenArgs

fun interface OnAcademyItemClick {
    fun onExecute(args: RegisterAcademyScreenArgs)
}