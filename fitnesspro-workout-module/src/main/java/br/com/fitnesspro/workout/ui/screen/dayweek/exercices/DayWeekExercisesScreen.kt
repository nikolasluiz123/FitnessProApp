package br.com.fitnesspro.workout.ui.screen.dayweek.exercices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.filter.SimpleFilter
import br.com.fitnesspro.compose.components.list.grouped.nested.NestedGroupedList
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.navigation.ExerciseScreenArgs
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.callbacks.OnNavigateExercise
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.DayWeekWorkoutItem
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.WorkoutGroupItem
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekExercisesUIState
import br.com.fitnesspro.workout.ui.viewmodel.DayWeekExercisesViewModel
import br.com.fitnesspro.workout.ui.viewmodel.WorkoutGroupEditDialogViewModel
import java.time.DayOfWeek

@Composable
fun DayWeekExercisesScreen(
    viewModel: DayWeekExercisesViewModel,
    onBackClick: () -> Unit,
    onNavigateExercise: OnNavigateExercise
) {
    val state by viewModel.uiState.collectAsState()
    val workoutGroupEditDialogViewModel = hiltViewModel<WorkoutGroupEditDialogViewModel>()

    DayWeekExercisesScreen(
        state = state,
        onBackClick = onBackClick,
        onNavigateExercise = onNavigateExercise,
        onUpdateExercises = viewModel::updateExercises,
        workoutGroupEditDialogViewModel = workoutGroupEditDialogViewModel,
        onDeleteWorkout = viewModel::deleteWorkout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayWeekExercisesScreen(
    state: DayWeekExercisesUIState = DayWeekExercisesUIState(),
    onBackClick: () -> Unit = { },
    onNavigateExercise: OnNavigateExercise? = null,
    onUpdateExercises: () -> Unit = { },
    workoutGroupEditDialogViewModel: WorkoutGroupEditDialogViewModel? = null,
    onDeleteWorkout: () -> Unit = {}
) {
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
                                onDeleteWorkout()
                            }
                        )
                    }
                }
            )
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

            if (state.showWorkoutGroupEditDialog) {
                WorkoutGroupEditDialog(
                    viewModel = workoutGroupEditDialogViewModel!!,
                    workoutGroupId = state.workoutGroupIdEdited!!,
                    onDismissRequest = state.onDismissWorkoutGroupEditDialog,
                    onSaveClick = { onUpdateExercises() },
                    onInactivateClick = { onUpdateExercises() }
                )
            }

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
                                },
                                onItemLongClick = {
                                    state.workoutGroupIdEdited = it.id
                                    state.onShowWorkoutGroupEditDialog()
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