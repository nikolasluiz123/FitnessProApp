package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.PreDefinitionScreen
import br.com.fitnesspro.workout.ui.viewmodel.PreDefinitionViewModel
import com.google.gson.GsonBuilder

internal const val preDefinitionScreenRoute = "preDefinitionScreenRoute"
internal const val preDefinitionScreenArguments = "preDefinitionScreenArguments"

fun NavGraphBuilder.preDefinitionScreen(
    onBackClick: () -> Unit,
) {
    composable(route = "$preDefinitionScreenRoute?$preDefinitionScreenArguments={$preDefinitionScreenArguments}") {
        val viewModel = hiltViewModel<PreDefinitionViewModel>()

        PreDefinitionScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToPreDefinitionScreen(args: PreDefinitionScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$preDefinitionScreenRoute?$preDefinitionScreenArguments={$json}", navOptions = navOptions)
}

data class PreDefinitionScreenArgs(
    val grouped: Boolean,
    val exercisePreDefinitionId: String? = null,
)