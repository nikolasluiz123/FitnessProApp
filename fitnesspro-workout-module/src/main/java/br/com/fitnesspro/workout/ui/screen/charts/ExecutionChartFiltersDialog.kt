package br.com.fitnesspro.workout.ui.screen.charts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.buttons.DefaultDialogTextButton
import br.com.fitnesspro.compose.components.buttons.icons.IconButtonClose
import br.com.fitnesspro.compose.components.fields.MultipleRadioButtonsField
import br.com.fitnesspro.core.theme.DialogTitleTextStyle
import br.com.fitnesspro.core.theme.LabelMediumTextStyle
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.ui.state.ExecutionChartFiltersDialogUIState

@Composable
fun ExecutionGroupedBarChartFiltersDialog(state: ExecutionChartFiltersDialogUIState) {
    if (state.showDialog) {
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
                    Modifier.fillMaxSize()
                ) {
                    val (headerRef, containerRef, buttonsContainerRef) = createRefs()

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
                            text = stringResource(R.string.execution_grouped_bar_chart_filters_dialog_title),
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

                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState)
                            .constrainAs(containerRef) {
                                top.linkTo(headerRef.bottom)
                                bottom.linkTo(buttonsContainerRef.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)

                                height = Dimension.fillToConstraints
                            }
                    ) {
                        Text(
                            text = stringResource(R.string.excution_grouped_bar_chart_filters_screen_label_chart_type),
                            style = LabelMediumTextStyle,
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        MultipleRadioButtonsField(
                            state = state.chartTypeRadioButtons,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = stringResource(R.string.excution_grouped_bar_chart_filters_screen_label_metric_value),
                            style = LabelMediumTextStyle,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )

                        MultipleRadioButtonsField(
                            state = state.metricValueRadioButtons,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = stringResource(R.string.excution_grouped_bar_chart_filters_screen_label_focus_value),
                            style = LabelMediumTextStyle,
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                        )

                        MultipleRadioButtonsField(
                            state = state.focusValueRadioButtons,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

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
                        DefaultDialogTextButton(
                            modifier = Modifier.align(Alignment.CenterStart),
                            labelResId = R.string.excution_grouped_bar_chart_filters_screen_restore_button,
                            onClick = {
                                state.onShowDialogChange(false)
                                state.onRestoreClick()
                            }
                        )


                        DefaultDialogTextButton(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            labelResId = R.string.excution_grouped_bar_chart_filters_screen_apply_button,
                            onClick = {
                                state.onShowDialogChange(false)
                                state.onApplyClick()
                            }
                        )
                    }
                }
            }
        }
    }
}