package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.registeruser.RegisterAcademyScreen
import br.com.fitnesspro.ui.viewmodel.RegisterAcademyViewModel

internal const val registerAcademyScreenRoute = "registerAcademy"


fun NavGraphBuilder.registerAcademyScreen(
    onBackClick: () -> Unit
) {
    composable(route = registerAcademyScreenRoute) {
        val viewModel = hiltViewModel<RegisterAcademyViewModel>()

        RegisterAcademyScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToRegisterAcademyScreen(navOptions: NavOptions? = null) {
    navigate(route = registerAcademyScreenRoute, navOptions = navOptions)
}