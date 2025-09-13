package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.workout.ui.screen.charts.ExecutionBarChartScreen
import br.com.fitnesspro.workout.ui.viewmodel.ExecutionBarChartViewModel

internal const val executionBarChartScreenRoute = "executionBarChartScreenRoute"

fun NavGraphBuilder.executionBarChartScreen() {
    composable(route = executionBarChartScreenRoute) {
        val viewModel = hiltViewModel<ExecutionBarChartViewModel>()

        ExecutionBarChartScreen()
    }
}

fun NavController.navigateToExecutionBarChartScreen(navOptions: NavOptions? = null) {
    navigate(route = executionBarChartScreenRoute, navOptions = navOptions)
}