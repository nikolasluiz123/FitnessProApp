package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.core.utils.gson.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.charts.ExecutionGroupedBarChartScreen
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionChartViewModel
import com.google.gson.GsonBuilder

internal const val executionChartScreenRoute = "executionChartScreenRoute"
internal const val executionChartScreenArguments = "executionChartScreenArguments"

fun NavGraphBuilder.executionChartScreen(
    onBackClick: () -> Unit
) {
    composable(route = "$executionChartScreenRoute?$executionChartScreenArguments={$executionChartScreenArguments}") {
        val viewModel = hiltViewModel<ExecutionChartViewModel>()

        ExecutionGroupedBarChartScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToExecutionChartScreen(args: ExecutionChartScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$executionChartScreenRoute?$executionChartScreenArguments={$json}", navOptions = navOptions)
}

data class ExecutionChartScreenArgs(
    val exerciseId: String
)