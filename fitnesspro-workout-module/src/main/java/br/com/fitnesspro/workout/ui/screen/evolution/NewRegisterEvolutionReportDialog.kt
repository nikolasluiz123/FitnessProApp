package br.com.fitnesspro.workout.ui.screen.evolution

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonClose
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.loading.FitnessProCircularBlockUIProgressIndicator
import br.com.fitnesspro.core.extensions.openPDFReader
import br.com.fitnesspro.core.theme.DialogTitleTextStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.screen.evolution.callbacks.OnGenerateWorkoutReportClick
import br.com.fitnesspro.workout.ui.state.reports.NewRegisterEvolutionReportDialogUIState
import br.com.fitnesspro.workout.ui.state.reports.WorkoutDialogListItem

@Composable
fun NewRegisterEvolutionReportDialog(
    state: NewRegisterEvolutionReportDialogUIState,
    onGenerateClick: OnGenerateWorkoutReportClick? = null
) {
    if (state.showDialog) {
        val context = LocalContext.current
        val softwareKeyboardController = LocalSoftwareKeyboardController.current

        Dialog(
            onDismissRequest = state.onDismissRequest,
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
                    val (headerRef, workoutRef, nameRef, buttonsContainerRef, loadingRef) = createRefs()

                    FitnessProCircularBlockUIProgressIndicator(
                        show = state.showLoading,
                        label = stringResource(R.string.new_register_evolution_report_dialog_loading_label),
                        modifier = Modifier.constrainAs(loadingRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                    )

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
                            text = stringResource(R.string.new_register_evolution_report_dialog_title),
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
                            onClick = state.onDismissRequest,
                            modifier = Modifier.constrainAs(closeButtonRef) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                        )
                    }

                    PagedListDialogOutlinedTextFieldValidation(
                        modifier = Modifier
                            .constrainAs(workoutRef) {
                                top.linkTo(headerRef.bottom, margin = 8.dp)
                                start.linkTo(parent.start, margin = 16.dp)
                                end.linkTo(parent.end, margin = 16.dp)

                                width = Dimension.fillToConstraints
                            },
                        field = state.workout,
                        fieldLabel = stringResource(R.string.new_register_evolution_report_dialog_label_workout),
                        simpleFilterPlaceholderResId = R.string.new_register_evolution_report_dialog_placeholder_search_workouts,
                        emptyMessage = R.string.new_register_evolution_report_dialog_empty_message_workouts,
                        itemLayout = {
                            WorkoutDialogListItem(
                                workout = it,
                                onItemClick = state.workout.dialogListState.onDataListItemClick
                            )
                        }
                    )

                    OutlinedTextFieldValidation(
                        field = state.name,
                        label = stringResource(R.string.new_register_evolution_report_dialog_name_label),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.constrainAs(nameRef) {
                            top.linkTo(workoutRef.bottom, margin = 8.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                            end.linkTo(parent.end, margin = 16.dp)
                            bottom.linkTo(buttonsContainerRef.top, margin = 8.dp)

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
                            labelResId = R.string.new_register_evolution_report_dialog_cancel_button,
                            onClick = {
                                state.onDismissRequest()
                            }
                        )


                        DialogTextButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            labelResId = R.string.new_register_evolution_report_dialog_generate_button,
                            onClick = {
                                onGenerateReport(state, onGenerateClick, context, softwareKeyboardController)
                            }
                        )
                    }
                }
            }
        }
    }
}

private fun onGenerateReport(
    state: NewRegisterEvolutionReportDialogUIState,
    onGenerateClick: OnGenerateWorkoutReportClick?,
    context: Context,
    softwareKeyboardController: SoftwareKeyboardController?
) {
    softwareKeyboardController?.hide()
    state.onToggleLoading()

    onGenerateClick?.onExecute { filePath ->
        state.onToggleLoading()
        state.onDismissRequest()
        context.openPDFReader(filePath)
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
fun NewSchedulerReportDialogPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            NewRegisterEvolutionReportDialog(
                state = NewRegisterEvolutionReportDialogUIState()
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
fun NewSchedulerReportDialogPreviewLight() {
    FitnessProTheme {
        Surface {
            NewRegisterEvolutionReportDialog(
                state = NewRegisterEvolutionReportDialogUIState()
            )
        }
    }
}