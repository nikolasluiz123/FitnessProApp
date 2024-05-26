package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.screen.login.LoginScreen
import br.com.fitnesspro.ui.screen.login.callback.OnBottomSheetRegisterUserItemClick
import br.com.fitnesspro.ui.viewmodel.LoginViewModel

internal const val loginScreenRoute = "login"

fun NavGraphBuilder.loginScreen(
    onBottomSheetRegisterUserItemClick: OnBottomSheetRegisterUserItemClick
) {
    composable(route = loginScreenRoute) {
        val loginViewModel = hiltViewModel<LoginViewModel>()

        LoginScreen(
            viewModel = loginViewModel,
            onBottomSheetRegisterUserItemClick = onBottomSheetRegisterUserItemClick
        )
    }
}

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) {
    navigate(route = loginScreenRoute, navOptions = navOptions)
}