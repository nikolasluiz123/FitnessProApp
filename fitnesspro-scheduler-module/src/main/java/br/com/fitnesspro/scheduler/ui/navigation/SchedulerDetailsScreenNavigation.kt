package br.com.fitnesspro.scheduler.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSonComposeNavigation
import br.com.fitnesspro.scheduler.ui.screen.scheduler.SchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.screen.scheduler.callback.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerDetailsViewModel
import com.google.gson.GsonBuilder
import java.time.LocalDate

internal const val schedulerDetailsScreenRoute = "schedulerDetails"
internal const val schedulerDetailsArguments = "schedulerDetailsArguments"


fun NavGraphBuilder.schedulerDetailsScreen(
    onBackClick: () -> Unit,
    onNavigateToCompromise: OnNavigateToCompromise
) {
    composable(route = "$schedulerDetailsScreenRoute?$schedulerDetailsArguments={$schedulerDetailsArguments}") {
        val registerUserViewModel = hiltViewModel<SchedulerDetailsViewModel>()

        SchedulerDetailsScreen(
            viewModel = registerUserViewModel,
            onBackClick = onBackClick,
            onNavigateToCompromise = onNavigateToCompromise
        )
    }
}

fun NavController.navigateToSchedulerDetailsScreen(args: SchedulerDetailsScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSonComposeNavigation().toJson(args)
    navigate(route = "$schedulerDetailsScreenRoute?$schedulerDetailsArguments={$json}", navOptions = navOptions)
}

class SchedulerDetailsScreenArgs(
    val scheduledDate: LocalDate,
)