package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.charts.ExecutionGroupedBarChartScreen
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionGroupedBarChartViewModel
import com.google.gson.GsonBuilder

internal const val executionGroupedBarChartScreenRoute = "executionGroupedBarChartScreenRoute"
internal const val executionGroupedBarChartScreenArguments = "executionGroupedBarChartScreenArguments"

fun NavGraphBuilder.executionGroupedBarChartScreen(
    onBackClick: () -> Unit
) {
    composable(route = "$executionGroupedBarChartScreenRoute?$executionGroupedBarChartScreenArguments={$executionGroupedBarChartScreenArguments}") {
        val viewModel = hiltViewModel<ExecutionGroupedBarChartViewModel>()

        ExecutionGroupedBarChartScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToExecutionGroupedBarChartScreen(args: ExecutionGroupedBarChartScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$executionGroupedBarChartScreenRoute?$executionGroupedBarChartScreenArguments={$json}", navOptions = navOptions)
}

data class ExecutionGroupedBarChartScreenArgs(
    val exerciseId: String
)