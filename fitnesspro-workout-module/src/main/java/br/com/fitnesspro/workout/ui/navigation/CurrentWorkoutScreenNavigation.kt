package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.workout.ui.screen.current.workout.CurrentWorkoutScreen
import br.com.fitnesspro.workout.ui.viewmodel.CurrentWorkoutViewModel


internal const val currentWorkoutScreenRoute = "currentWorkoutScreenRoute"


fun NavGraphBuilder.currentWorkoutScreen(
    onBackClick: () -> Unit,
) {
    composable(route = currentWorkoutScreenRoute) {
        val viewModel = hiltViewModel<CurrentWorkoutViewModel>()

        CurrentWorkoutScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToCurrentWorkoutScreen(navOptions: NavOptions? = null) {
    navigate(route = currentWorkoutScreenRoute, navOptions = navOptions)
}