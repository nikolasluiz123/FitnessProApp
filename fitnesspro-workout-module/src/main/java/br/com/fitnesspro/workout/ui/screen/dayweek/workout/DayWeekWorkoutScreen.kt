package br.com.fitnesspro.workout.ui.screen.dayweek.workout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.grouped.LazyGroupedVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.ExerciseDetailsScreenArgs
import br.com.fitnesspro.workout.ui.screen.details.callbacks.OnNavigateToExerciseDetails
import br.com.fitnesspro.workout.ui.state.DayWeekWorkoutUIState
import br.com.fitnesspro.workout.ui.viewmodel.DayWeekWorkoutViewModel

@Composable
fun DayWeekWorkoutScreen(
    viewModel: DayWeekWorkoutViewModel,
    onBackClick: () -> Unit,
    onNavigateToExerciseDetails: OnNavigateToExerciseDetails
) {
    val state by viewModel.uiState.collectAsState()

    DayWeekWorkoutScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateToExerciseDetails = onNavigateToExerciseDetails
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWeekWorkoutScreen(
    state: DayWeekWorkoutUIState,
    onBackClick: () -> Unit = {},
    onNavigateToExerciseDetails: OnNavigateToExerciseDetails? = null
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        ConstraintLayout(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val (workoutList) = createRefs()

            FitnessProMessageDialog(state = state.messageDialogState)

            LazyGroupedVerticalList(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(workoutList) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                groups = state.dayWeekWorkoutGroups,
                emptyMessageResId = R.string.day_week_workout_empty_message,
                groupLayout = { groupDecorator ->
                    WorkoutGroupItem(
                        decorator = groupDecorator,
                        enabled = false
                    )
                },
                itemLayout = { itemDecorator ->
                    DayWeekWorkoutItem(
                        toExercise = itemDecorator,
                        onItemClick = {
                            onNavigateToExerciseDetails?.onNavigate(
                                args = ExerciseDetailsScreenArgs(exerciseId = it.id!!)
                            )
                        }
                    )
                }
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun DayWeekWorkoutScreenPreview() {
    FitnessProTheme {
        Surface {
            DayWeekWorkoutScreen(dayWeekWorkoutScreenDefaultState)
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
fun DayWeekWorkoutScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekWorkoutScreen(
                state = dayWeekWorkoutScreenDefaultState
            )
        }
    }
}