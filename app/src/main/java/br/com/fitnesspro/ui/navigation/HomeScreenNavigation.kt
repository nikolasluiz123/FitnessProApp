package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.home.HomeScreen
import br.com.fitnesspro.ui.screen.home.OnNavigateToAccountInformation
import br.com.fitnesspro.ui.viewmodel.HomeViewModel

internal const val homeScreenRoute = "home"

fun NavGraphBuilder.homeScreen(
    onNavigateToAccountInformation: OnNavigateToAccountInformation
) {
    composable(route = homeScreenRoute) {
        val loginViewModel = hiltViewModel<HomeViewModel>()

        HomeScreen(
            viewModel = loginViewModel,
            onNavigateToAccountInformation = onNavigateToAccountInformation
        )
    }
}

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) {
    navigate(route = homeScreenRoute, navOptions = navOptions)
}