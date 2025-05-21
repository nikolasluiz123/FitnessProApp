package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.workout.ui.screen.members.workout.MembersWorkoutScreen
import br.com.fitnesspro.workout.ui.viewmodel.MembersWorkoutViewModel


internal const val membersWorkoutScreenRoute = "membersWorkoutScreenRoute"


fun NavGraphBuilder.membersWorkoutScreen(
    onBackClick: () -> Unit,
) {
    composable(route = membersWorkoutScreenRoute) {
        val viewModel = hiltViewModel<MembersWorkoutViewModel>()

        MembersWorkoutScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToMembersWorkoutScreen(navOptions: NavOptions? = null) {
    navigate(route = membersWorkoutScreenRoute, navOptions = navOptions)
}