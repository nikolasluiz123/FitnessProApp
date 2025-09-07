package br.com.fitnesspro.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.ui.screen.home.HomeScreen
import br.com.fitnesspro.ui.screen.home.callbacks.OnNavigateToAccountInformation
import br.com.fitnesspro.ui.viewmodel.HomeViewModel

const val homeScreenRoute = "home"

fun NavGraphBuilder.homeScreen(
    onNavigateToAccountInformation: OnNavigateToAccountInformation,
    onNavigateToSchedule: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToMembersWorkoutScreen: () -> Unit,
    onNavigateToCurrentWorkoutScreen: () -> Unit,
    onNavigateToPreDefinitionsScreen: () -> Unit,
    onNavigateToMemberEvolutionScreen: () -> Unit
) {
    composable(route = homeScreenRoute) {
        val loginViewModel = hiltViewModel<HomeViewModel>()

        HomeScreen(
            viewModel = loginViewModel,
            onNavigateToAccountInformation = onNavigateToAccountInformation,
            onNavigateToSchedule = onNavigateToSchedule,
            onNavigateToLogin = onNavigateToLogin,
            onNavigateToMembersWorkoutScreen = onNavigateToMembersWorkoutScreen,
            onNavigateToCurrentWorkoutScreen = onNavigateToCurrentWorkoutScreen,
            onNavigateToPreDefinitionsScreen = onNavigateToPreDefinitionsScreen,
            onNavigateToMemberEvolutionScreen = onNavigateToMemberEvolutionScreen
        )
    }
}

fun NavController.navigateToHomeScreen(navOptions: NavOptions? = null) {
    navigate(route = homeScreenRoute, navOptions = navOptions)
}