package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.core.utils.gson.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.evolution.RegisterEvolutionScreen
import br.com.fitnesspro.workout.ui.viewmodel.RegisterEvolutionViewModel
import com.google.gson.GsonBuilder

internal const val registerEvolutionScreenRoute = "registerEvolutionScreenRoute"
internal const val registerEvolutionScreenArguments = "registerEvolutionScreenArguments"

fun NavGraphBuilder.registerEvolutionScreen(
    onBackClick: () -> Unit,
) {
    composable(route = "$registerEvolutionScreenRoute?$registerEvolutionScreenArguments={$registerEvolutionScreenArguments}") {
        val viewModel = hiltViewModel< RegisterEvolutionViewModel>()

        RegisterEvolutionScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToRegisterEvolutionScreen(args: RegisterEvolutionScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$registerEvolutionScreenRoute?$registerEvolutionScreenArguments={$json}", navOptions = navOptions)
}

data class RegisterEvolutionScreenArgs(
    val exerciseId: String,
    val exerciseExecutionId: String? = null,
)