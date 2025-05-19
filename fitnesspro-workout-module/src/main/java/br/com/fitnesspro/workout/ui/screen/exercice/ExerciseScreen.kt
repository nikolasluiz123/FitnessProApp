package br.com.fitnesspro.workout.ui.screen.exercice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.ExerciseUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(state: ExerciseUIState = ExerciseUIState()) {
    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title!!,
                subtitle = state.subtitle,
                onBackClick = {

                }
            )
        },
        bottomBar = {
            FitnessProBottomAppBar(
                modifier = Modifier.imePadding(),
                actions = {
                    IconButtonDelete(
                        onClick = {

                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButtonSave(
                        iconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        onClick = {

                        }
                    )
                }
            )
        },
        snackbarHost = {

        }
    ) { paddings ->
        val scrollState = rememberScrollState()
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(Modifier.fillMaxSize()) {
            FitnessProLinearProgressIndicator(state.showLoading)

            FitnessProMessageDialog(state.messageDialogState)

            ConstraintLayout(
                Modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .padding(horizontal = 8.dp)
                    .consumeWindowInsets(paddings)
                    .verticalScroll(scrollState)
                    .imePadding()
            ) {
                val (groupRef, exerciseRef, setsRef, repsRef, restRef, unitRestRef,
                    durationRef, unitDurationRef, observationRef) = createRefs()

                DefaultExposedDropdownMenu(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(groupRef) {
                            top.linkTo(parent.top, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    field = state.group,
                    labelResId = R.string.exercise_screen_label_group
                )

                PagedListDialogOutlinedTextFieldValidation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(exerciseRef) {
                            top.linkTo(groupRef.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    field = state.exercise,
                    fieldLabel = stringResource(R.string.exercise_screen_label_exercise),
                    simpleFilterPlaceholderResId = R.string.exercise_screen_exercises_place_holder,
                    emptyMessage = R.string.exercise_screen_exercises_empty_message,
                    itemLayout = {
                        ExercisePagedDialogItem(it)
                    }
                )

                createHorizontalChain(setsRef, repsRef)

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .constrainAs(setsRef) {
                        start.linkTo(parent.start)
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    field = state.sets,
                    label = stringResource(R.string.exercise_screen_label_sets),

                    )

                OutlinedTextFieldValidation(
                    modifier = Modifier.constrainAs(repsRef) {
                        end.linkTo(parent.end)
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    field = state.reps,
                    label = stringResource(R.string.exercise_screen_label_reps),
                )

                createHorizontalChain(restRef, unitRestRef)

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .constrainAs(restRef) {
                        start.linkTo(parent.start)
                        top.linkTo(setsRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    field = state.rest,
                    label = stringResource(R.string.exercise_screen_label_rest),

                    )

                DefaultExposedDropdownMenu(
                    modifier = Modifier.constrainAs(unitRestRef) {
                        end.linkTo(parent.end)
                        top.linkTo(repsRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    field = state.unitRest,
                    labelResId = R.string.exercise_screen_label_unit,
                )

                createHorizontalChain(durationRef, unitDurationRef)

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .constrainAs(durationRef) {
                        start.linkTo(parent.start)
                        top.linkTo(restRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    field = state.duration,
                    label = stringResource(R.string.exercise_screen_label_duration),
                )

                DefaultExposedDropdownMenu(
                    modifier = Modifier.constrainAs(unitDurationRef) {
                        end.linkTo(parent.end)
                        top.linkTo(unitRestRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                    field = state.unitDuration,
                    labelResId = R.string.exercise_screen_label_unit,
                )

                OutlinedTextFieldValidation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(observationRef) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(durationRef.bottom, margin = 8.dp)
                        },
                    field = state.duration,
                    label = stringResource(R.string.exercise_screen_label_observation),
                )
            }
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenNewPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreen(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenNewPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreen(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreen(
                state = exerciseUIState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun ExerciseScreenPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreen(
                state = exerciseUIState
            )
        }
    }
}