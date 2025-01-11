package br.com.fitnesspro.scheduler.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.scheduler.ui.screen.scheduler.SchedulerConfigScreen
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerConfigViewModel

internal const val schedulerConfigScreenRoute = "schedulerConfig"


fun NavGraphBuilder.schedulerConfigScreen(
    onBackClick: () -> Unit
) {
    composable(route = schedulerConfigScreenRoute) {
        val viewModel = hiltViewModel<SchedulerConfigViewModel>()

        SchedulerConfigScreen(
            viewModel = viewModel,
            onNavigateBack = onBackClick
        )
    }
}

fun NavController.navigateToSchedulerConfigScreen(navOptions: NavOptions? = null) {
    navigate(route = schedulerConfigScreenRoute, navOptions = navOptions)
}