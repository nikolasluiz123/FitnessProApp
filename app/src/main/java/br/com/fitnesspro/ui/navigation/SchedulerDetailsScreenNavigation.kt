package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSonComposeNavigation
import br.com.fitnesspro.ui.screen.scheduler.SchedulerDetailsScreen
import br.com.fitnesspro.ui.viewmodel.SchedulerDetailsViewModel
import com.google.gson.GsonBuilder
import java.time.LocalDate

internal const val schedulerDetailsScreenRoute = "schedulerDetails"
internal const val schedulerDetailsArguments = "schedulerDetailsArguments"


fun NavGraphBuilder.schedulerDetailsScreen(
    onBackClick: () -> Unit,
) {
    composable(route = "$schedulerDetailsScreenRoute?$schedulerDetailsArguments={$schedulerDetailsArguments}") {
        val registerUserViewModel = hiltViewModel<SchedulerDetailsViewModel>()

        SchedulerDetailsScreen(
            viewModel = registerUserViewModel,
            onBackClick = onBackClick
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