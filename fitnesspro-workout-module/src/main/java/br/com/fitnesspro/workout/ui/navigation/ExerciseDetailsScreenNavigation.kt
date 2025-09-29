package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.core.utils.gson.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.details.ExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnNavigateToRegisterEvolution
import br.com.fitnesspro.workout.ui.viewmodel.ExerciseDetailsViewModel
import com.google.gson.GsonBuilder

internal const val exerciseDetailsScreenRoute = "exerciseDetailsScreenRoute"
internal const val exerciseDetailsScreenArguments = "exerciseDetailsScreenArguments"

fun NavGraphBuilder.exerciseDetailsScreen(
    onBackClick: () -> Unit,
    onNavigateToRegisterEvolution: OnNavigateToRegisterEvolution
) {
    composable(route = "$exerciseDetailsScreenRoute?$exerciseDetailsScreenArguments={$exerciseDetailsScreenArguments}") {
        val viewModel = hiltViewModel<ExerciseDetailsViewModel>()

        ExerciseDetailsScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
            onNavigateToRegisterEvolution = onNavigateToRegisterEvolution
        )
    }
}

fun NavController.navigateToExerciseDetailsScreen(args: ExerciseDetailsScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$exerciseDetailsScreenRoute?$exerciseDetailsScreenArguments={$json}", navOptions = navOptions)
}

data class ExerciseDetailsScreenArgs(
    val exerciseId: String
)