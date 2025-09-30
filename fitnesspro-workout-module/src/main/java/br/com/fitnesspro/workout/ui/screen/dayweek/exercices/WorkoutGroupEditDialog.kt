package br.com.fitnesspro.workout.ui.screen.dayweek.exercices

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.android.ui.compose.components.fields.dropdown.DefaultExposedDropdownMenu
import br.com.android.ui.compose.components.fields.text.OutlinedTextFieldValidation
import br.com.android.ui.compose.components.loading.BaseCircularBlockUIProgressIndicator
import br.com.android.ui.compose.components.styles.DialogTitleTextStyle
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonClose
import br.com.fitnesspro.compose.components.dialog.FitnessProMessageDialog
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.callbacks.OnInactivateWorkoutGroupClick
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.callbacks.OnSaveWorkoutGroupClick
import br.com.fitnesspro.workout.ui.state.WorkoutGroupEditDialogUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun WorkoutGroupEditDialog(
    state: WorkoutGroupEditDialogUIState,
    onSaveClick: OnSaveWorkoutGroupClick? = null,
    onInactivateClick: OnInactivateWorkoutGroupClick? = null,
    onLoadDataEdition: () -> Unit = { },
    snackbarHostState: SnackbarHostState? = null,
    coroutineScope: CoroutineScope? = null,
    context: Context? = null
) {
    if (state.showDialog) {
        LaunchedEffect(Unit) {
            onLoadDataEdition()
        }

        Dialog(
            onDismissRequest = { state.onShowDialogChange(false) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.padding(16.dp)
            ) {
                ConstraintLayout(
                    Modifier
                        .fillMaxWidth()
                ) {
                    val (headerRef, nameRef, dayWeekRef, orderRef, buttonsContainerRef, loadingRef) = createRefs()

                    BaseCircularBlockUIProgressIndicator(
                        show = state.showLoading,
                        label = stringResource(R.string.workout_group_edit_dialog_loading_label),
                        modifier = Modifier.constrainAs(loadingRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                    )

                    FitnessProMessageDialog(state = state.messageDialogState)

                    ConstraintLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp)
                            .constrainAs(headerRef) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                    ) {
                        val (titleRef, closeButtonRef) = createRefs()

                        Text(
                            text = state.title,
                            style = DialogTitleTextStyle,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.constrainAs(titleRef) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(closeButtonRef.start)

                                width = Dimension.fillToConstraints
                            }
                        )

                        IconButtonClose(
                            iconColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { state.onShowDialogChange(false) },
                            modifier = Modifier.constrainAs(closeButtonRef) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                        )
                    }

                    OutlinedTextFieldValidation(
                        field = state.name,
                        label = stringResource(R.string.workout_group_edit_dialog_name_label),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.constrainAs(nameRef) {
                            top.linkTo(headerRef.bottom, margin = 8.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)

                            width = Dimension.fillToConstraints
                        }
                    )

                    OutlinedTextFieldValidation(
                        field = state.order,
                        label = stringResource(R.string.workout_group_edit_dialog_order_label),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.constrainAs(orderRef) {
                            top.linkTo(nameRef.bottom, margin = 8.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)

                            width = Dimension.fillToConstraints
                        }
                    )

                    DefaultExposedDropdownMenu(
                        field = state.dayWeek,
                        labelResId = R.string.workout_group_edit_dialog_day_week_label,
                        modifier = Modifier.constrainAs(dayWeekRef) {
                            top.linkTo(orderRef.bottom, 8.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            bottom.linkTo(buttonsContainerRef.top)

                            width = Dimension.fillToConstraints
                        }
                    )

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .constrainAs(buttonsContainerRef) {
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                    ) {
                        DialogTextButton(
                            modifier = Modifier.align(Alignment.CenterStart),
                            labelResId = R.string.workout_group_edit_dialog_inactivate_button,
                            onClick = {
                                onInactivateClick?.onExecute {
                                    state.onToggleLoading()
                                    state.onShowDialogChange(false)

                                    coroutineScope?.launch {
                                        val message = context?.getString(R.string.workout_group_edit_dialog_msg_inactivated_success)!!
                                        snackbarHostState?.showSnackbar(message)
                                    }
                                }
                            }
                        )


                        DialogTextButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            labelResId = R.string.workout_group_edit_dialog_save_button,
                            onClick = {
                                state.onToggleLoading()
                                onSaveClick?.onExecute {
                                    state.onToggleLoading()
                                    state.onShowDialogChange(false)

                                    coroutineScope?.launch {
                                        val message = context?.getString(R.string.workout_group_edit_dialog_msg_save_success)!!
                                        snackbarHostState?.showSnackbar(message)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogTextButton(
    labelResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        ),
        onClick = {
            onClick()
        }
    ) {
        Text(text = stringResource(id = labelResId))
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun WorkoutGroupEditDialogPreview() {
    FitnessProTheme {
        Surface {
            WorkoutGroupEditDialog(
                state = workoutGroupEditDialogDefaultState,
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun WorkoutGroupEditDialogPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            WorkoutGroupEditDialog(
                state = workoutGroupEditDialogDefaultState,
            )
        }
    }
}