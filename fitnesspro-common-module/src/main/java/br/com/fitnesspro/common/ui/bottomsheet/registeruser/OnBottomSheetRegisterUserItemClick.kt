package br.com.fitnesspro.common.ui.bottomsheet.registeruser

import br.com.fitnesspro.common.ui.navigation.RegisterUserScreenArgs

fun interface OnBottomSheetRegisterUserItemClick {
    fun onNavigate(args: RegisterUserScreenArgs)
}