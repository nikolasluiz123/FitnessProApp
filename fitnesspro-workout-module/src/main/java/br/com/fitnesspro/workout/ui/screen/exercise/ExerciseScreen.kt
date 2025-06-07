package br.com.fitnesspro.workout.ui.screen.exercise

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.bottombar.FitnessProBottomAppBar
import br.com.fitnesspro.compose.components.buttons.fab.FloatingActionButtonSave
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonDelete
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.compose.components.fields.ListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.menu.DefaultExposedDropdownMenu
import br.com.fitnesspro.compose.components.loading.FitnessProLinearProgressIndicator
import br.com.fitnesspro.compose.components.topbar.SimpleFitnessProTopAppBar
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.SnackBarTextStyle
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.exercise.callbacks.OnSaveExerciseClick
import br.com.fitnesspro.workout.ui.screen.exercise.enums.EnumExerciseScreenTags
import br.com.fitnesspro.workout.ui.state.ExerciseUIState
import br.com.fitnesspro.workout.ui.viewmodel.ExerciseViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ExerciseScreen(
    viewModel: ExerciseViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    ExerciseScreen(
        state = state,
        onBackClick = onBackClick,
        onSaveExerciseClick = viewModel::saveExercise
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseScreen(
    state: ExerciseUIState = ExerciseUIState(),
    onBackClick: () -> Unit = {},
    onSaveExerciseClick: OnSaveExerciseClick? = null
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            SimpleFitnessProTopAppBar(
                title = state.title,
                subtitle = state.subtitle,
                onBackClick = onBackClick
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
                            onSave(keyboardController, state, onSaveExerciseClick, coroutineScope, snackbarHostState, context)
                        }
                    )
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
                            onSave(keyboardController, state, onSaveExerciseClick, coroutineScope, snackbarHostState, context)
                        }
                    )
                )
            }
        }
    }
}

private fun onSave(
    keyboardController: SoftwareKeyboardController?,
    state: ExerciseUIState,
    onSaveExerciseClick: OnSaveExerciseClick?,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    context: Context
) {
    keyboardController?.hide()
    state.onToggleLoading()
    Firebase.analytics.logButtonClick(EnumExerciseScreenTags.EXERCISE_SCREEN_KEYBOARD_SAVE)

    onSaveExerciseClick?.onExecute {
        state.onToggleLoading()
        showSuccessMessage(coroutineScope, snackbarHostState, context)
    }
}

fun showSuccessMessage(coroutineScope: CoroutineScope, snackbarHostState: SnackbarHostState, context: Context) {
    coroutineScope.launch {
        val message = context.getString(R.string.exercise_screen_success_message)
        snackbarHostState.showSnackbar(message = message)
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