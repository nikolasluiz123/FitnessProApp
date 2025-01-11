package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.scheduler.SchedulerScreen
import br.com.fitnesspro.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.ui.screen.scheduler.callback.OnNavigateToCompromise
import br.com.fitnesspro.ui.viewmodel.SchedulerViewModel

internal const val schedulerScreenRoute = "scheduler"

fun NavGraphBuilder.schedulerScreen(
    onBackClick: () -> Unit,
    onDayClick: OnDayClick,
    onNavigateToCompromise: OnNavigateToCompromise,
    onNavigateToConfig: () -> Unit
) {
    composable(route = schedulerScreenRoute) {
        val viewModel = hiltViewModel<SchedulerViewModel>()

        SchedulerScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onDayClick = onDayClick,
            onNavigateToCompromise = onNavigateToCompromise,
            onNavigateToConfig = onNavigateToConfig
        )
    }
}

fun NavController.navigateToScheduleScreen(navOptions: NavOptions? = null) {
    navigate(route = schedulerScreenRoute, navOptions = navOptions)
}