package br.com.fitnesspro.scheduler.ui.navigation

import android.net.Uri
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.scheduler.ui.screen.details.SchedulerDetailsScreen
import br.com.fitnesspro.scheduler.ui.screen.details.callbacks.OnNavigateToCompromise
import br.com.fitnesspro.scheduler.ui.viewmodel.SchedulerDetailsViewModel
import com.google.gson.GsonBuilder
import java.time.LocalDate

internal const val schedulerDetailsScreenRoute = "schedulerDetails"
internal const val schedulerDetailsArguments = "schedulerDetailsArguments"
private const val schedulerDetailsDeeplinkRoute = "app://fitnesspro/schedulerDetails?"


fun NavGraphBuilder.schedulerDetailsScreen(
    onBackClick: () -> Unit,
    onNavigateToCompromise: OnNavigateToCompromise
) {
    composable(
        route = "$schedulerDetailsScreenRoute?$schedulerDetailsArguments={$schedulerDetailsArguments}",
        deepLinks = listOf(navDeepLink { uriPattern = "$schedulerDetailsDeeplinkRoute$schedulerDetailsArguments={$schedulerDetailsArguments}" })
    ) {
        val registerUserViewModel = hiltViewModel<SchedulerDetailsViewModel>()

        SchedulerDetailsScreen(
            viewModel = registerUserViewModel,
            onBackClick = onBackClick,
            onNavigateToCompromise = onNavigateToCompromise
        )
    }
}

fun NavController.navigateToSchedulerDetailsScreen(args: SchedulerDetailsScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$schedulerDetailsScreenRoute?$schedulerDetailsArguments={$json}", navOptions = navOptions)
}

fun getSchedulerDetailsScreenDeepLinkUri(args: SchedulerDetailsScreenArgs): Uri {
    val json = GsonBuilder().defaultGSon().toJson(args)
    return "$schedulerDetailsDeeplinkRoute$schedulerDetailsArguments={$json}".toUri()
}

class SchedulerDetailsScreenArgs(
    val scheduledDate: LocalDate,
)