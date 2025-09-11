package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.workout.ui.screen.evolution.MembersEvolutionScreen
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnNavigateToExecutionEvolutionHistory
import br.com.fitnesspro.workout.ui.viewmodel.MembersEvolutionViewModel

internal const val membersEvolutionScreenRout = "membersEvolutionScreenRout"

fun NavGraphBuilder.membersEvolutionScreen(
    onBackClick: () -> Unit,
    onNavigateToExecutionEvolutionHistory: OnNavigateToExecutionEvolutionHistory
) {
    composable(route = membersEvolutionScreenRout) {
        val viewModel = hiltViewModel<MembersEvolutionViewModel>()

        MembersEvolutionScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNavigateToExecutionEvolutionHistory = onNavigateToExecutionEvolutionHistory
        )
    }
}

fun NavController.navigateToMembersEvolutionScreen(navOptions: NavOptions? = null) {
    navigate(route = membersEvolutionScreenRout, navOptions = navOptions)
}