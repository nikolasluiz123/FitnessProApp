package br.com.fitnesspro.common.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.core.utils.gson.extensions.defaultGSon
import br.com.fitnesspro.common.ui.screen.report.GeneratedReportsScreen
import br.com.fitnesspro.common.ui.viewmodel.GeneratedReportsViewModel
import br.com.fitnesspro.model.enums.EnumReportContext
import com.google.gson.GsonBuilder

internal const val generatedReportsScreenRoute = "generatedReportsScreenRoute"
internal const val generatedReportsArguments = "generatedReportsArguments"

fun NavGraphBuilder.generatedReportsScreen(
    onNavigateBackClick: () -> Unit,
) {
    composable(route = "$generatedReportsScreenRoute?$generatedReportsArguments={$generatedReportsArguments}") {
        val viewModel = hiltViewModel<GeneratedReportsViewModel>()

        GeneratedReportsScreen(
            viewModel = viewModel,
            onNavigateBackClick = onNavigateBackClick,
        )
    }
}

fun NavController.navigateToGeneratedReportsScreen(args: GeneratedReportsScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)

    navigate(route = "$generatedReportsScreenRoute?$generatedReportsArguments={$json}", navOptions = navOptions)
}

class GeneratedReportsScreenArgs(
    val subtitle: String,
    val reportContext: EnumReportContext
)
