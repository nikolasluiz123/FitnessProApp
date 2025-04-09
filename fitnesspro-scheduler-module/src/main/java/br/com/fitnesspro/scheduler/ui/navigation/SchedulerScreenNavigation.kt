package br.com.fitnesspro.scheduler.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.scheduler.ui.screen.scheduler.SchedulerScreen
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.scheduler.ui.screen.details.callbacks.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerViewModel

const val schedulerScreenRoute = "scheduler"

fun NavGraphBuilder.schedulerScreen(
    onBackClick: () -> Unit,
    onDayClick: OnDayClick,
    onNavigateToCompromise: OnNavigateToCompromise,
    onNavigateToConfig: () -> Unit,
    onNavigateToChatHistory: () -> Unit
) {
    composable(route = schedulerScreenRoute) {
        val viewModel = hiltViewModel<SchedulerViewModel>()

        SchedulerScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onDayClick = onDayClick,
            onNavigateToCompromise = onNavigateToCompromise,
            onNavigateToConfig = onNavigateToConfig,
            onNavigateToChatHistory = onNavigateToChatHistory
        )
    }
}

fun NavController.navigateToScheduleScreen(navOptions: NavOptions? = null) {
    navigate(route = schedulerScreenRoute, navOptions = navOptions)
}