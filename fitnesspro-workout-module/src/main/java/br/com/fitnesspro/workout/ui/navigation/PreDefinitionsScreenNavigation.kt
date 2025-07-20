package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.callbacks.OnNavigateToPreDefinition
import br.com.fitnesspro.workout.ui.screen.predefinitions.search.PreDefinitionsScreen
import br.com.fitnesspro.workout.ui.viewmodel.PreDefinitionsViewModel

internal const val preDefinitionsScreenRoute = "preDefinitionsScreenRoute"

fun NavGraphBuilder.preDefinitionsScreen(
    onBackClick: () -> Unit,
    onNavigateToPreDefinition: OnNavigateToPreDefinition
) {
    composable(route = preDefinitionsScreenRoute) {
        val viewModel = hiltViewModel<PreDefinitionsViewModel>()

        PreDefinitionsScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNavigateToPreDefinition = onNavigateToPreDefinition
        )
    }
}

fun NavController.navigateToPreDefinitionsScreen(navOptions: NavOptions? = null) {
    navigate(route = preDefinitionsScreenRoute, navOptions = navOptions)
}