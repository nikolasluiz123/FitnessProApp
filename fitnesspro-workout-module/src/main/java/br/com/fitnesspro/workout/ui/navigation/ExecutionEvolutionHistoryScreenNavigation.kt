package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import br.com.fitnesspro.workout.ui.screen.evolution.ExecutionEvolutionHistoryScreen
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionEvolutionHistoryViewModel
import com.google.gson.GsonBuilder

internal const val executionEvolutionHistoryScreenRoute = "executionEvolutionHistoryScreenRoute"
internal const val executionEvolutionHistoryScreenRouteArguments = "executionEvolutionHistoryScreenRouteArguments"


fun NavGraphBuilder.executionEvolutionHistoryScreen(
    onBackClick: () -> Unit,
    onHistoryClick: (ExecutionEvolutionHistoryGroupedTuple) -> Unit
) {
    composable(route = "$executionEvolutionHistoryScreenRoute?$executionEvolutionHistoryScreenRouteArguments={$executionEvolutionHistoryScreenRouteArguments}") {
        val viewModel = hiltViewModel<ExecutionEvolutionHistoryViewModel>()

        ExecutionEvolutionHistoryScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onHistoryClick = onHistoryClick
        )
    }
}

fun NavController.navigateToExecutionEvolutionHistoryScreen(args: ExecutionEvolutionHistoryScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$executionEvolutionHistoryScreenRoute?$executionEvolutionHistoryScreenRouteArguments={$json}", navOptions = navOptions)
}

data class ExecutionEvolutionHistoryScreenArgs(
    val personMemberId: String
)