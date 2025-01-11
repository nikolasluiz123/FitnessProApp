package br.com.fitnesspro.common.ui.screen.registeruser.callback

import br.com.fitnesspro.common.ui.navigation.RegisterAcademyScreenArgs

fun interface OnAcademyItemClick {
    fun onExecute(args: RegisterAcademyScreenArgs)
}