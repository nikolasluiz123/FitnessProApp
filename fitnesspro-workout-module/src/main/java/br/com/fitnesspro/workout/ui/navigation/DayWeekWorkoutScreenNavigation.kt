package br.com.fitnesspro.workout.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.DayWeekWorkoutScreen
import br.com.fitnesspro.workout.ui.viewmodel.DayWeekWorkoutViewModel
import com.google.gson.GsonBuilder
import java.time.DayOfWeek


internal const val dayWeekWorkoutScreenRoute = "dayWeekWorkoutScreenRoute"
internal const val dayWeekWorkoutScreenArguments = "dayWeekWorkoutScreenArguments"


fun NavGraphBuilder.dayWeekWorkoutScreen(
    onBackClick: () -> Unit,
) {
    composable(route = "$dayWeekWorkoutScreenRoute?$dayWeekWorkoutScreenArguments={$dayWeekWorkoutScreenArguments}") {
        val viewModel = hiltViewModel<DayWeekWorkoutViewModel>()

        DayWeekWorkoutScreen(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToDayWeekWorkoutScreen(args: DayWeekWorkoutScreenArgs, navOptions: NavOptions? = null) {
    val json = GsonBuilder().defaultGSon().toJson(args)
    navigate(route = "$dayWeekWorkoutScreenRoute?$dayWeekWorkoutScreenArguments={$json}", navOptions = navOptions)
}

data class DayWeekWorkoutScreenArgs(
    val workoutId: String,
    val workoutGroupId: String? = null,
    val dayWeek: DayOfWeek? = null,
)