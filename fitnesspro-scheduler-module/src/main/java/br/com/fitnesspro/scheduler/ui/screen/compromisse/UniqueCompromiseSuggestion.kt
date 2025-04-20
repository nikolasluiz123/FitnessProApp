package br.com.fitnesspro.scheduler.ui.screen.compromisse

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.LabeledText
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.TimePickerOutlinedTextFieldValidation
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_DATA_CANCEL
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_HOUR
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_NAME
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_OBSERVATION
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_PROFESSIONAL
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_SITUATION
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_PROFESSIONAL_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState

@Composable
fun UniqueCompromiseSuggestion(state: CompromiseUIState) {
    if (state.toScheduler.id != null) {
        UniqueCompromiseSuggestionReadOnly(state)
    } else {
        UniqueCompromiseSuggestionEditable(state)
    }
}

@Composable
fun UniqueCompromiseSuggestionReadOnly(state: CompromiseUIState) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        val (nameRef, hourRef, professionalRef, situationRef, observationRef, dataCancelRef) = createRefs()

        createHorizontalChain(nameRef, professionalRef)

        LabeledText(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_LABELED_TEXT_NAME.name)
                .constrainAs(nameRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.compromise_screen_label_name),
            value = state.toScheduler.professionalName!!
        )

        LabeledText(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_LABELED_TEXT_PROFESSIONAL.name)
                .constrainAs(professionalRef) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.compromise_screen_label_professional_type),
            value = state.toScheduler.professionalType?.getLabel(context)!!
        )

        createHorizontalChain(hourRef, situationRef)

        LabeledText(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_LABELED_TEXT_HOUR.name)
                .constrainAs(hourRef) {
                    top.linkTo(nameRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.compromise_screen_label_hour),
            value = stringResource(
                R.string.compromise_screen_label_hour_value,
                state.toScheduler.timeStart!!.format(TIME),
                state.toScheduler.timeEnd!!.format(TIME)
            )
        )

        LabeledText(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_LABELED_TEXT_SITUATION.name)
                .constrainAs(situationRef) {
                    top.linkTo(professionalRef.bottom, margin = 8.dp)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.compromise_screen_label_situation),
            value = state.toScheduler.situation?.getLabel(context)!!
        )

        if (state.toScheduler.situation == EnumSchedulerSituation.CANCELLED) {
            LabeledText(
                modifier = Modifier
                    .testTag(COMPROMISE_SCREEN_LABELED_TEXT_DATA_CANCEL.name)
                    .constrainAs(dataCancelRef) {
                        top.linkTo(situationRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                label = stringResource(R.string.compromise_screen_label_cancel),
                value = state.toScheduler.canceledDate?.format(DATE)!!
            )
        }

        state.toScheduler.observation?.let {
            LabeledText(
                modifier = Modifier
                    .testTag(COMPROMISE_SCREEN_LABELED_TEXT_OBSERVATION.name)
                    .constrainAs(observationRef) {
                        val isCanceled = state.toScheduler.situation == EnumSchedulerSituation.CANCELLED
                        val topAnchor = if (isCanceled) dataCancelRef else situationRef

                        top.linkTo(topAnchor.bottom, margin = 8.dp)
                        start.linkTo(parent.start)
                    },
                label = stringResource(R.string.compromise_screen_label_observation),
                value = it
            )
        }
    }
}

@Composable
private fun UniqueCompromiseSuggestionEditable(state: CompromiseUIState) {
    val scrollState = rememberScrollState()
    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .scrollable(scrollState, orientation = Orientation.Vertical)
    ) {
        val (professionalRef, startRef, endRef, observationRef) = createRefs()

        PagedListDialogOutlinedTextFieldValidation(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_PROFESSIONAL_FIELD.name)
                .constrainAs(professionalRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth(),
            field = state.professional,
            fieldLabel = stringResource(R.string.compromise_screen_label_professional),
            simpleFilterPlaceholderResId = R.string.compromise_screen_simple_filter_placeholder_professional_dialog_list,
            emptyMessage = R.string.compromise_screen_empty_message_professional_dialog_list,
            itemLayout = { personTuple ->
                PersonDialogListItem(
                    person = personTuple,
                    onItemClick = state.professional.dialogListState.onDataListItemClick
                )
            }
        )

        TimePickerOutlinedTextFieldValidation(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_START_HOUR_FIELD.name)
                .constrainAs(startRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(professionalRef.bottom, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
            field = state.hourStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_time_start),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_start_hour),
        )

        TimePickerOutlinedTextFieldValidation(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_END_HOUR_FIELD.name)
                .constrainAs(endRef) {
                    top.linkTo(startRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
            field = state.hourEnd,
            fieldLabel = stringResource(R.string.compromise_screen_label_time_end),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_end_hour),
            keyboardActions = KeyboardActions(
                onDone = {

                }
            )
        )

        OutlinedTextFieldValidation(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_OBSERVATION_FIELD.name)
                .constrainAs(observationRef) {
                    top.linkTo(endRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
            field = state.observation,
            label = stringResource(R.string.compromise_screen_label_observation),
            keyboardActions = KeyboardActions(
                onDone = {

                }
            )
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromiseSuggestionPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            UniqueCompromiseSuggestion(
                state = defaultCompromiseAcademyMemberState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromiseSuggestionEditionPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            UniqueCompromiseSuggestion(
                state = compromiseAcademyMemberEditionState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromiseSuggestionPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            UniqueCompromiseSuggestion(
                state = defaultCompromiseAcademyMemberState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromiseSuggestionEditionPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            UniqueCompromiseSuggestion(
                state = compromiseAcademyMemberEditionState
            )
        }
    }
}