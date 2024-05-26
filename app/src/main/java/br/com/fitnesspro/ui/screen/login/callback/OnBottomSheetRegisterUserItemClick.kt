package br.com.fitnesspro.ui.screen.login.callback

import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs

fun interface OnBottomSheetRegisterUserItemClick {
    fun onNavigate(args: RegisterUserScreenArgs)
}