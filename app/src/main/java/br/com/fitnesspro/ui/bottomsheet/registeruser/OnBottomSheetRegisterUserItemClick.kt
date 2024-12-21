package br.com.fitnesspro.ui.bottomsheet.registeruser

import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs

fun interface OnBottomSheetRegisterUserItemClick {
    fun onNavigate(args: RegisterUserScreenArgs)
}