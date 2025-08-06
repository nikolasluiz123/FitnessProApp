package br.com.fitnesspro.workout.ui.screen.current.workout

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.list.LazyVerticalList
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.DayWeekWorkoutScreenArgs
import br.com.fitnesspro.workout.ui.screen.current.workout.callbacks.OnNavigateToDayWeekWorkout
import br.com.fitnesspro.workout.ui.state.CurrentWorkoutUIState
import br.com.fitnesspro.workout.ui.viewmodel.CurrentWorkoutViewModel

@Composable
fun CurrentWorkoutScreen(
    viewModel: CurrentWorkoutViewModel,
    onBackClick: () -> Unit,
    onNavigateToDayWeekWorkout: OnNavigateToDayWeekWorkout
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    CurrentWorkoutScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateToDayWeekWorkout = onNavigateToDayWeekWorkout,
        onUpdateItems = viewModel::loadItems,
        onExecuteLoad = viewModel::loadWorkout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentWorkoutScreen(
    state: CurrentWorkoutUIState,
    onBackClick: () -> Unit = {},
    onNavigateToDayWeekWorkout: OnNavigateToDayWeekWorkout? = null,
    onUpdateItems: () -> Unit = {},
    onExecuteLoad: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = stringResource(R.string.current_workout_title),
                subtitle = state.subtitle,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            val (listRef) = createRefs()

            LaunchedEffect(Unit) {
                onUpdateItems()
            }

            LaunchedEffect(state.executeLoad) {
                if (state.executeLoad) {
                    onExecuteLoad()
                }
            }

            FitnessProMessageDialog(state = state.messageDialogState)

            LazyVerticalList(
                modifier = Modifier
                    .fillMaxSize()
                    .constrainAs(listRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                items = state.items,
                emptyMessageResId = R.string.current_workout_empty_message
            ) { listItem ->
                CurrentWorkoutItem(
                    decorator = listItem,
                    onItemClick = { clickedItem ->
                        state.toWorkout?.id?.let { workoutId ->
                            onNavigateToDayWeekWorkout?.onNavigate(
                                args = DayWeekWorkoutScreenArgs(
                                    workoutId = workoutId,
                                    dayWeek = clickedItem.dayWeek
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun CurrentWorkoutScreenPreview() {
    FitnessProTheme {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutState
            )
        }
    }
}


@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun CurrentWorkoutScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun CurrentWorkoutScreenEmptyPreview() {
    FitnessProTheme {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun CurrentWorkoutScreenEmptyPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            CurrentWorkoutScreen(
                state = currentWorkoutEmptyState
            )
        }
    }
}