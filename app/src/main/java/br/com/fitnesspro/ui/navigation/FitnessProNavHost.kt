package br.com.fitnesspro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost

@Composable
fun FitnessProNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = loginScreenRoute,
        modifier = modifier
    ) {

        loginScreen(
            onBottomSheetRegisterUserItemClick = navController::navigateToRegisterUserScreen,
            onNavigateToHome = {
                navController.navigateToHomeScreen(
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(loginScreenRoute, inclusive = true)
                        .build()
                )
            }
        )

        registerUserScreen(
            onBackClick = navController::popBackStack,
            onAddAcademyClick = navController::navigateToRegisterAcademyScreen,
            onAcademyItemClick = navController::navigateToRegisterAcademyScreen
        )

        registerAcademyScreen(
            onBackClick = navController::popBackStack,
        )

        homeScreen(
            onMyInformationsClick = navController::navigateToRegisterUserScreen,
            onLogoutClick = {
                navController.navigateToLoginScreen(
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(homeScreenRoute, inclusive = true)
                        .build()
                )
            }
        )

    }
}