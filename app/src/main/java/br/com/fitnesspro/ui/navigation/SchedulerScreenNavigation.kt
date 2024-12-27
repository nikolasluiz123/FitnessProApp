package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.scheduler.SchedulerScreen
import br.com.fitnesspro.ui.viewmodel.ScheduleViewModel

internal const val schedulerScreenRoute = "scheduler"

fun NavGraphBuilder.schedulerScreen(
    onBackClick: () -> Unit,
) {
    composable(route = schedulerScreenRoute) {
        val registerUserViewModel = hiltViewModel<ScheduleViewModel>()

        SchedulerScreen(
            viewModel = registerUserViewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToScheduleScreen(navOptions: NavOptions? = null) {
    navigate(route = schedulerScreenRoute, navOptions = navOptions)
}