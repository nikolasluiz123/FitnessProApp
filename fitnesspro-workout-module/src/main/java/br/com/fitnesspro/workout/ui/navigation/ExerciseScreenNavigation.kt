package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.exercise.ExerciseScreen
import br.com.fitnesspro.workout.ui.viewmodel.ExerciseViewModel
import com.google.gson.GsonBuilder
import java.time.DayOfWeek


internal const val exerciseScreenRoute = "exercisesScreenRoute"
internal const val exerciseScreenArguments = "exercisesScreenArguments"


fun NavGraphBuilder.exercisesScreen(
    onBackClick: () -> Unit,
) {
    composable(route = "$exerciseScreenRoute?$exerciseScreenArguments={$exerciseScreenArguments}") {
        val viewModel = hiltViewModel<ExerciseViewModel>()

        ExerciseScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToExercisesScreen(args: ExerciseScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$exerciseScreenRoute?$exerciseScreenArguments={$json}", navOptions = navOptions)
}

data class ExerciseScreenArgs(
    val workoutId: String,
    val workoutGroupId: String? = null,
    val dayWeek: DayOfWeek? = null,
    val exerciseId: String? = null
)