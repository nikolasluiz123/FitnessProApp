package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSonComposeNavigation
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.screen.registeruser.RegisterUserScreen
import br.com.fitnesspro.ui.screen.registeruser.callback.OnAcademyItemClick
import br.com.fitnesspro.ui.screen.registeruser.callback.OnAddAcademy
import br.com.fitnesspro.ui.viewmodel.RegisterUserViewModel
import com.google.gson.GsonBuilder

internal const val registerUserScreenRoute = "registerUser"
internal const val registerUserArguments = "registerUserArguments"

fun NavGraphBuilder.registerUserScreen(
    onBackClick: () -> Unit,
    onAddAcademyClick: OnAddAcademy,
    onAcademyItemClick: OnAcademyItemClick
) {
    composable(route = "$registerUserScreenRoute?$registerUserArguments={$registerUserArguments}") {
        val registerUserViewModel = hiltViewModel<RegisterUserViewModel>()

        RegisterUserScreen(
            viewModel = registerUserViewModel,
            onBackClick = onBackClick,
            onAddAcademyClick = onAddAcademyClick,
            onAcademyItemClick = onAcademyItemClick
        )
    }
}

fun NavController.navigateToRegisterUserScreen(args: RegisterUserScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSonComposeNavigation().toJson(args)

    navigate(route = "$registerUserScreenRoute?$registerUserArguments={$json}", navOptions = navOptions)
}

class RegisterUserScreenArgs(
    val context: EnumOptionsBottomSheetRegisterUser? = null,
)