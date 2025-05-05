package br.com.fitnesspro.scheduler.ui.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import br.com.fitnesspro.scheduler.ui.screen.details.callbacks.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.screen.scheduler.SchedulerScreen
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnDayClick
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerViewModel

const val schedulerScreenRoute = "scheduler"
private const val schedulerRoute = "app://fitnesspro/scheduler"

fun NavGraphBuilder.schedulerScreen(
    onBackClick: () -> Unit,
    onDayClick: OnDayClick,
    onNavigateToCompromise: OnNavigateToCompromise,
    onNavigateToConfig: () -> Unit,
    onNavigateToChatHistory: () -> Unit
) {
    composable(
        route = schedulerScreenRoute,
        deepLinks = listOf(navDeepLink { uriPattern = schedulerRoute })
    ) {
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

fun getSchedulerScreenDeepLinkUri(): Uri {
    return schedulerRoute.toUri()
}

fun NavController.navigateToScheduleScreen(navOptions: NavOptions? = null) {
    navigate(route = schedulerScreenRoute, navOptions = navOptions)
}