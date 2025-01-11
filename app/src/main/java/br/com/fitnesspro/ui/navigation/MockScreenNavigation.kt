package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.mock.MockScreen
import br.com.fitnesspro.ui.viewmodel.MockViewModel

internal const val mockScreenRoute = "mock"

fun NavGraphBuilder.mockScreen(
) {
    composable(route = mockScreenRoute) {
        val loginViewModel = hiltViewModel<MockViewModel>()

        MockScreen(viewModel = loginViewModel)
    }
}

fun NavController.navigateToMockScreen(navOptions: NavOptions? = null) {
    navigate(route = mockScreenRoute, navOptions = navOptions)
}