package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.schedule.ScheduleScreen
import br.com.fitnesspro.ui.viewmodel.ScheduleViewModel

internal const val scheduleScreenRoute = "schedule"

fun NavGraphBuilder.scheduleScreen(
    onBackClick: () -> Unit,
) {
    composable(route = scheduleScreenRoute) {
        val registerUserViewModel = hiltViewModel<ScheduleViewModel>()

        ScheduleScreen(
            viewModel = registerUserViewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToScheduleScreen(navOptions: NavOptions? = null) {
    navigate(route = scheduleScreenRoute, navOptions = navOptions)
}