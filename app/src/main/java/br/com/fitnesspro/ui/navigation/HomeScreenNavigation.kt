package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.home.HomeScreen
import br.com.fitnesspro.ui.screen.home.callback.OnMyInformationsClick
import br.com.fitnesspro.ui.viewmodel.HomeViewModel

internal const val homeScreenRoute = "home"

fun NavGraphBuilder.homeScreen(
    onMyInformationsClick: OnMyInformationsClick
) {
    composable(route = homeScreenRoute) {
        val viewModel = hiltViewModel<HomeViewModel>()

        HomeScreen(
            viewModel = viewModel,
            onMyInformationsClick = onMyInformationsClick
        )
    }
}

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) {
    navigate(route = homeScreenRoute, navOptions = navOptions)
}