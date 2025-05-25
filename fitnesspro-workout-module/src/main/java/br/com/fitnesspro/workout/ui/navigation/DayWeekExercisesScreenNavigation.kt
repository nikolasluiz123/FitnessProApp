package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.DayWeekExercisesScreen
import br.com.fitnesspro.workout.ui.viewmodel.DayWeekExercisesViewModel
import com.google.gson.GsonBuilder


internal const val dayWeekExercisesScreenRoute = "dayWeekExercisesScreenRoute"
internal const val dayWeekExercisesScreenArguments = "dayWeekExercisesScreenArguments"


fun NavGraphBuilder.dayWeekExercisesScreen(
    onBackClick: () -> Unit,
) {
    composable(route = "$dayWeekExercisesScreenRoute?$dayWeekExercisesScreenArguments={$dayWeekExercisesScreenArguments}") {
        val viewModel = hiltViewModel<DayWeekExercisesViewModel>()

        DayWeekExercisesScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToDayWeekExercisesScreen(args: DayWeekExercisesScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$dayWeekExercisesScreenRoute?$dayWeekExercisesScreenArguments={$json}", navOptions = navOptions)
}

data class DayWeekExercisesScreenArgs(
    val workoutId: String
)