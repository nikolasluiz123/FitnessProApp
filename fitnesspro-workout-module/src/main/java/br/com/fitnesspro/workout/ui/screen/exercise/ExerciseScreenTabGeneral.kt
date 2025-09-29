package br.com.fitnesspro.workout.ui.screen.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.android.ui.compose.components.fields.dropdown.DefaultExposedDropdownMenu
import br.com.android.ui.compose.components.fields.text.OutlinedTextFieldValidation
import br.com.android.ui.compose.components.fields.text.dialog.ListDialogOutlinedTextFieldValidation
import br.com.android.ui.compose.components.fields.text.dialog.paged.PagedListDialogOutlinedTextFieldValidation
import br.com.android.ui.compose.components.loading.BaseLinearProgressIndicator
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.ExerciseUIState

@Composable
fun ExerciseScreenTabGeneral(
    state: ExerciseUIState = ExerciseUIState(),
    onDone: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(Modifier.fillMaxSize()) {
        BaseLinearProgressIndicator(show = state.showLoading)

        ConstraintLayout(
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
        ) {
            val (groupRef, exerciseRef, setsRef, repsRef, restRef, unitRestRef,
                durationRef, unitDurationRef, observationRef, groupOrderRef, exerciseOrderRef) = createRefs()

            ListDialogOutlinedTextFieldValidation(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(groupRef) {
                        top.linkTo(parent.top, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                field = state.group,
                fieldLabel = stringResource(R.string.exercise_screen_label_group),
                simpleFilterPlaceholderResId = R.string.exercise_screen_group_place_holder,
                emptyMessage = R.string.exercise_screen_group_empty_message,
                itemLayout = {
                    GroupDialogItem(
                        toWorkoutGroup = it,
                        onItemClick = state.group.dialogListState.onDataListItemClick
                    )
                }
            )

            OutlinedTextFieldValidation(
                modifier = Modifier
                    .constrainAs(groupOrderRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(groupRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                field = state.groupOrder,
                label = stringResource(R.string.exercise_screen_label_group_order),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            PagedListDialogOutlinedTextFieldValidation(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(exerciseRef) {
                        top.linkTo(groupOrderRef.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                field = state.exercise,
                fieldLabel = stringResource(R.string.exercise_screen_label_exercise),
                simpleFilterPlaceholderResId = R.string.exercise_screen_exercises_place_holder,
                emptyMessage = R.string.exercise_screen_exercises_empty_message,
                itemLayout = {
                    ExercisePagedDialogItem(
                        toExercise = it,
                        onItemClick = state.exercise.dialogListState.onDataListItemClick
                    )
                }
            )

            OutlinedTextFieldValidation(
                modifier = Modifier
                    .constrainAs(exerciseOrderRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(exerciseRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                field = state.exerciseOrder,
                label = stringResource(R.string.exercise_screen_label_exercise_order),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextFieldValidation(
                modifier = Modifier
                    .constrainAs(setsRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(exerciseOrderRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                field = state.sets,
                label = stringResource(R.string.exercise_screen_label_sets),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextFieldValidation(
                modifier = Modifier.constrainAs(repsRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(setsRef.bottom, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
                field = state.reps,
                label = stringResource(R.string.exercise_screen_label_reps),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextFieldValidation(
                modifier = Modifier
                    .constrainAs(restRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(repsRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                field = state.rest,
                label = stringResource(R.string.exercise_screen_label_rest),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )

                )

            DefaultExposedDropdownMenu(
                modifier = Modifier.constrainAs(unitRestRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(restRef.bottom, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
                field = state.unitRest,
                labelResId = R.string.exercise_screen_label_unit,
                showClearOption = true,
                clearOptionText = stringResource(R.string.exercise_screen_label_clear_unit)
            )

            OutlinedTextFieldValidation(
                modifier = Modifier
                    .constrainAs(durationRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(unitRestRef.bottom, margin = 8.dp)

                        width = Dimension.fillToConstraints
                    },
                field = state.duration,
                label = stringResource(R.string.exercise_screen_label_duration),
            )

            DefaultExposedDropdownMenu(
                modifier = Modifier.constrainAs(unitDurationRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(durationRef.bottom, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
                field = state.unitDuration,
                labelResId = R.string.exercise_screen_label_unit,
                showClearOption = true,
                clearOptionText = stringResource(R.string.exercise_screen_label_clear_unit)
            )

            OutlinedTextFieldValidation(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(observationRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(unitDurationRef.bottom, margin = 8.dp)
                    },
                field = state.observation,
                label = stringResource(R.string.exercise_screen_label_observation),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onDone()
                    }
                )
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenTabGeneralNewPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreenTabGeneral(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenTabGeneralNewPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreenTabGeneral(
                state = exerciseNewUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenTabGeneralPreview() {
    FitnessProTheme {
        Surface {
            ExerciseScreenTabGeneral(
                state = exerciseTabGeneralUIState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun ExerciseScreenTabGeneralPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            ExerciseScreenTabGeneral(
                state = exerciseTabGeneralUIState
            )
        }
    }
}