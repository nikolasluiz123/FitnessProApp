package br.com.fitnesspro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
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
        )

        registerUserScreen()
    }
}