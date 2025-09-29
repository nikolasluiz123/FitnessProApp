package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.core.utils.gson.extensions.defaultGSon
import br.com.fitnesspro.common.ui.screen.report.callback.OnNavigateToReports
import br.com.fitnesspro.workout.ui.screen.evolution.ExecutionEvolutionHistoryScreen
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnNavigateToExecutionGroupedBarChart
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionEvolutionHistoryViewModel
import com.google.gson.GsonBuilder

internal const val executionEvolutionHistoryScreenRoute = "executionEvolutionHistoryScreenRoute"
internal const val executionEvolutionHistoryScreenRouteArguments = "executionEvolutionHistoryScreenRouteArguments"


fun NavGraphBuilder.executionEvolutionHistoryScreen(
    onBackClick: () -> Unit,
    onHistoryClick: OnNavigateToExecutionGroupedBarChart,
    onNavigateToReports: OnNavigateToReports
) {
    composable(route = "$executionEvolutionHistoryScreenRoute?$executionEvolutionHistoryScreenRouteArguments={$executionEvolutionHistoryScreenRouteArguments}") {
        val viewModel = hiltViewModel<ExecutionEvolutionHistoryViewModel>()

        ExecutionEvolutionHistoryScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onHistoryClick = onHistoryClick,
            onNavigateToReports = onNavigateToReports
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