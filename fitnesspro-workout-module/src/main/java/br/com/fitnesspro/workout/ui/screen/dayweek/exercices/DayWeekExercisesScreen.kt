package br.com.fitnesspro.workout.ui.screen.dayweek.exercices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.grouped.nested.NestedGroupedList
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.ExerciseScreenArgs
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.callbacks.OnInactivateWorkoutClick
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.callbacks.OnInactivateWorkoutGroupClick
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.callbacks.OnNavigateExercise
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.callbacks.OnSaveWorkoutGroupClick
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.DayWeekWorkoutItem
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.WorkoutGroupItem
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekExercisesUIState
import br.com.fitnesspro.workout.ui.viewmodel.DayWeekExercisesViewModel
import java.time.DayOfWeek

@Composable
fun DayWeekExercisesScreen(
    viewModel: DayWeekExercisesViewModel,
    onBackClick: () -> Unit,
    onNavigateExercise: OnNavigateExercise
) {
    val state by viewModel.uiState.collectAsState()

    DayWeekExercisesScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateExercise = onNavigateExercise,
        onUpdateExercises = viewModel::updateExercises,
        onInactivateWorkoutClick = viewModel::onInactivateWorkout,
        onSaveWorkoutGroupClick = viewModel::onSaveWorkoutGroup,
        onInactivateWorkoutGroupClick = viewModel::onInactivateWorkoutGroup,
        onLoadDataWorkoutGroupEdition = viewModel::onLoadDataWorkoutGroupEdition
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWeekExercisesScreen(
    state: DayWeekExercisesUIState = DayWeekExercisesUIState(),
    onBackClick: () -> Unit = { },
    onNavigateExercise: OnNavigateExercise? = null,
    onUpdateExercises: () -> Unit = { },
    onInactivateWorkoutClick: OnInactivateWorkoutClick? = null,
    onSaveWorkoutGroupClick: OnSaveWorkoutGroupClick? = null,
    onInactivateWorkoutGroupClick: OnInactivateWorkoutGroupClick? = null,
    onLoadDataWorkoutGroupEdition: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick,
                actions = {
                    if (!state.isOverDue) {
                        IconButtonDelete(
                            enabled = state.deleteEnabled,
                            onClick = {
                                onInactivateWorkoutClick?.onExecute {
                                    state.onToggleLoading()
                                    onBackClick()
                                }
                            }
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                Snackbar(modifier = Modifier.padding(8.dp)) {
                    Text(text = it.visuals.message, style = SnackBarTextStyle)
                }
            }
        }
    ) { paddings ->

        Column(
            Modifier
                .padding(paddings)
                .consumeWindowInsets(paddings)
                .fillMaxSize()
        ) {
            LaunchedEffect(Unit) {
                onUpdateExercises()
            }

            FitnessProLinearProgressIndicator(state.showLoading)

            FitnessProMessageDialog(state.messageDialogState)

            WorkoutGroupEditDialog(
                state = state.workoutGroupEditDialogUIState,
                onSaveClick = onSaveWorkoutGroupClick,
                onInactivateClick = onInactivateWorkoutGroupClick,
                onLoadDataEdition = onLoadDataWorkoutGroupEdition,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                context = context
            )

            SimpleFilter(
                modifier = Modifier.fillMaxWidth(),
                state = state.simpleFilterState,
                placeholderResId = R.string.day_week_exercises_simple_filter_placeholder
            ) {
                NestedGroupedList(
                    modifier = Modifier.fillMaxSize(),
                    rootGroups = state.filteredGroups,
                    onGroup = { group, depth ->
                        when (depth) {
                            0 -> {
                                group as DayWeekExercicesGroupDecorator
                                DayWeekWorkoutGroupItem(
                                    decorator = group,
                                    onItemClick = {
                                        val args = ExerciseScreenArgs(
                                            workoutId = state.workout?.id!!,
                                            dayWeek = DayOfWeek.valueOf(it.id)
                                        )

                                        onNavigateExercise?.onExecute(args)
                                    }
                                )
                            }

                            1 -> {
                                group as WorkoutGroupDecorator
                                WorkoutGroupItem(
                                    decorator = group,
                                    onItemClick = {
                                        val args = ExerciseScreenArgs(
                                            workoutId = state.workout?.id!!,
                                            workoutGroupId = it.id,
                                        )

                                        onNavigateExercise?.onExecute(args)
                                    }
                                )
                            }
                        }
                    },
                    onItem = { item, depth ->
                        item as TOExercise
                        DayWeekWorkoutItem(
                            toExercise = item,
                            onItemClick = {
                                val args = ExerciseScreenArgs(
                                    workoutId = state.workout?.id!!,
                                    exerciseId = it.id
                                )

                                onNavigateExercise?.onExecute(args)
                            }
                        )
                    },
                    emptyMessageResId = R.string.day_week_exercises_empty_message
                )
            }

            NestedGroupedList(
                modifier = Modifier.fillMaxSize(),
                rootGroups = state.filteredGroups,
                onGroup = { group, depth ->
                    when (depth) {
                        0 -> {
                            group as DayWeekExercicesGroupDecorator
                            DayWeekWorkoutGroupItem(
                                decorator = group,
                                onItemClick = {
                                    val args = ExerciseScreenArgs(
                                        workoutId = state.workout?.id!!,
                                        dayWeek = DayOfWeek.valueOf(it.id)
                                    )

                                    onNavigateExercise?.onExecute(args)
                                },
                                enabled = !state.isOverDue
                            )
                        }

                        1 -> {
                            group as WorkoutGroupDecorator
                            WorkoutGroupItem(
                                decorator = group,
                                onItemClick = {
                                    val args = ExerciseScreenArgs(
                                        workoutId = state.workout?.id!!,
                                        workoutGroupId = it.id,
                                    )

                                    onNavigateExercise?.onExecute(args)
                                },
                                onItemLongClick = {
                                    state.workoutGroupIdEdited = it.id
                                    state.workoutGroupEditDialogUIState.onShowDialogChange(true)
                                },
                                enabled = !state.isOverDue
                            )
                        }
                    }
                },
                onItem = { item, depth ->
                    item as TOExercise
                    DayWeekWorkoutItem(
                        toExercise = item,
                        onItemClick = {
                            val args = ExerciseScreenArgs(
                                workoutId = state.workout?.id!!,
                                exerciseId = it.id
                            )

                            onNavigateExercise?.onExecute(args)
                        },
                        enabled = !state.isOverDue
                    )
                },
                emptyMessageResId = R.string.day_week_exercises_empty_message
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekExercisesScreenPreview() {
    FitnessProTheme {
        Surface {
            DayWeekExercisesScreen(
                state = dayWeekExercisesDefaultState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekExercisesScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekExercisesScreen(
                state = dayWeekExercisesDefaultState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekExercisesScreenOverDuePreview() {
    FitnessProTheme {
        Surface {
            DayWeekExercisesScreen(
                state = dayWeekExercisesOverDueState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekExercisesScreenOverDuePreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekExercisesScreen(
                state = dayWeekExercisesOverDueState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekExercisesScreenEmptyPreview() {
    FitnessProTheme {
        Surface {
            DayWeekExercisesScreen(
                state = dayWeekExercisesDefaultEmptyState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun DayWeekExercisesScreenEmptyPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            DayWeekExercisesScreen(
                state = dayWeekExercisesDefaultEmptyState
            )
        }
    }
}