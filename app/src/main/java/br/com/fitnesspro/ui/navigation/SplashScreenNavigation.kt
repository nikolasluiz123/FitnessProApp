package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.splash.SplashScreen
import br.com.fitnesspro.ui.viewmodel.SplashViewModel

const val splashScreenRoute = "splash"

fun NavGraphBuilder.splashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    composable(route = splashScreenRoute) {
        val viewModel = hiltViewModel<SplashViewModel>()

        SplashScreen(
            viewModel = viewModel,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToHome = onNavigateToHome
        )
    }
}