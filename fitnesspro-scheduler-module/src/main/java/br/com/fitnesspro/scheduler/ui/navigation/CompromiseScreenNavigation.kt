package br.com.fitnesspro.scheduler.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.scheduler.ui.screen.compromisse.CompromiseScreen
import br.com.fitnesspro.scheduler.ui.viewmodel.CompromiseViewModel
import com.google.gson.GsonBuilder
import java.time.LocalDate


internal const val compromiseScreenRoute = "compromise"
internal const val compromiseArguments = "compromiseArguments"


fun NavGraphBuilder.compromiseScreen(
    onBackClick: () -> Unit
) {
    composable(route = "$compromiseScreenRoute?$compromiseArguments={$compromiseArguments}") {
        val viewModel = hiltViewModel<CompromiseViewModel>()

        CompromiseScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToCompromiseScreen(
    args: CompromiseScreenArgs,
    navOptions: NavOptions? = null
) {
    val json = GsonBuilder().defaultGSon().toJson(args)

    navigate(route = "$compromiseScreenRoute?$compromiseArguments={$json}", navOptions = navOptions)
}

class CompromiseScreenArgs(
    val recurrent: Boolean,
    val date: LocalDate? = null,
    val schedulerId: String? = null
)