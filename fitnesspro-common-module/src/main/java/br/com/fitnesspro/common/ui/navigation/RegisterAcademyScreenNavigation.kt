package br.com.fitnesspro.common.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.common.ui.viewmodel.RegisterAcademyViewModel
import br.com.fitnesspro.core.extensions.defaultGSonComposeNavigation
import br.com.fitnesspro.common.ui.screen.registeruser.RegisterAcademyScreen
import com.google.gson.GsonBuilder

internal const val registerAcademyScreenRoute = "registerAcademy"
internal const val registerAcademyArguments = "registerAcademyArguments"


fun NavGraphBuilder.registerAcademyScreen(
    onBackClick: () -> Unit
) {
    composable(route = "$registerAcademyScreenRoute?$registerAcademyArguments={$registerAcademyArguments}") {
        val viewModel = hiltViewModel<RegisterAcademyViewModel>()

        RegisterAcademyScreen(
            viewModel = viewModel,
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToRegisterAcademyScreen(
    args: RegisterAcademyScreenArgs,
    navOptions: NavOptions? = null
) {
    val json = GsonBuilder().defaultGSonComposeNavigation().toJson(args)

    navigate(route = "$registerAcademyScreenRoute?$registerAcademyArguments={$json}", navOptions = navOptions)
}

class RegisterAcademyScreenArgs(
    val personId: String,
    val personAcademyTimeId: String? = null,
)