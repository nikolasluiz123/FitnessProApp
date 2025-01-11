package br.com.fitnesspro.common.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.common.ui.bottomsheet.registeruser.OnBottomSheetRegisterUserItemClick
import br.com.fitnesspro.common.ui.viewmodel.LoginViewModel
import br.com.fitnesspro.common.ui.screen.login.LoginScreen

const val loginScreenRoute = "login"

fun NavGraphBuilder.loginScreen(
    onBottomSheetRegisterUserItemClick: OnBottomSheetRegisterUserItemClick,
    onNavigateToHome: () -> Unit,
    onNavigateToMockScreen: () -> Unit
) {
    composable(route = loginScreenRoute) {
        val loginViewModel = hiltViewModel<LoginViewModel>()

        LoginScreen(
            viewModel = loginViewModel,
            onBottomSheetRegisterUserItemClick = onBottomSheetRegisterUserItemClick,
            onNavigateToHome = onNavigateToHome,
            onNavigateToMockScreen = onNavigateToMockScreen
        )
    }
}

fun NavController.navigateToLoginScreen(navOptions: NavOptions? = null) {
    navigate(route = loginScreenRoute, navOptions = navOptions)
}