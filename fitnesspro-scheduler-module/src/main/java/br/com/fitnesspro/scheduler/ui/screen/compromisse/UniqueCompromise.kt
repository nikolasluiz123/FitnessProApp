package br.com.fitnesspro.scheduler.ui.screen.compromisse

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.firebase.api.analytics.logButtonClick
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_CANCELLATION_PERSON
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_DATA_CANCEL
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_HOUR
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_MEMBER
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_OBSERVATION
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_LABELED_TEXT_SITUATION
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_SAVE_KEYBOARD_DONE
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
fun UniqueCompromise(
    state: CompromiseUIState,
    onKeyboardDone: () -> Unit = { }
) {
    if (state.toScheduler.situation == EnumSchedulerSituation.CANCELLED) {
        UniqueCompromiseReadOnly(state)
    } else {
        UniqueCompromiseEditable(state, onKeyboardDone)
    }
}
@Composable
fun UniqueCompromiseReadOnly(state: CompromiseUIState) {
    val context = LocalContext.current

    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .imePadding()
    ) {
        val (memberRef, hourRef, situationRef, observationRef, dataCancelRef, personCancelRef) = createRefs()

        LabeledText(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_LABELED_TEXT_MEMBER.name)
                .constrainAs(memberRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                },
            label = stringResource(R.string.compromise_screen_label_member_readonly),
            value = state.toScheduler.academyMemberName!!
        )

        createHorizontalChain(hourRef, situationRef)

        LabeledText(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_LABELED_TEXT_HOUR.name)
                .constrainAs(hourRef) {
                    top.linkTo(memberRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.compromise_screen_label_hour),
            value = stringResource(
                R.string.compromise_screen_label_hour_value,
                state.toScheduler.dateTimeStart!!.format(TIME),
                state.toScheduler.dateTimeEnd!!.format(TIME)
            )
        )

        LabeledText(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_LABELED_TEXT_SITUATION.name)
                .constrainAs(situationRef) {
                    top.linkTo(memberRef.bottom, margin = 8.dp)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                    horizontalChainWeight = 0.5f
                },
            label = stringResource(R.string.compromise_screen_label_situation),
            value = state.toScheduler.situation?.getLabel(context)!!
        )

        if (state.toScheduler.situation == EnumSchedulerSituation.CANCELLED) {
            createHorizontalChain(personCancelRef, dataCancelRef)

            LabeledText(
                modifier = Modifier
                    .testTag(COMPROMISE_SCREEN_LABELED_TEXT_CANCELLATION_PERSON.name)
                    .constrainAs(personCancelRef) {
                        top.linkTo(situationRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        horizontalChainWeight = 0.5f
                    },
                label = stringResource(R.string.compromise_screen_label_cancel_person),
                value = getPersonCancellation(state)
            )

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
                value = state.toScheduler.canceledDate?.format(EnumDateTimePatterns.DATE_TIME)!!
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
private fun UniqueCompromiseEditable(
    state: CompromiseUIState,
    onKeyboardDone: () -> Unit
) {
    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .imePadding()
    ) {
        val (memberRef, startRef, endRef, observationRef) = createRefs()

        PagedListDialogOutlinedTextFieldValidation(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_MEMBER_FIELD.name)
                .constrainAs(memberRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth(),
            field = state.member,
            fieldLabel = stringResource(R.string.compromise_screen_label_member),
            simpleFilterPlaceholderResId = R.string.compromise_screen_simple_filter_placeholder_member_dialog_list,
            emptyMessage = R.string.compromise_screen_empty_message_member_dialog_list,
            itemLayout = { personTuple ->
                PersonDialogListItem(
                    person = personTuple,
                    onItemClick = state.member.dialogListState.onDataListItemClick
                )
            }
        )

        TimePickerOutlinedTextFieldValidation(
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_START_HOUR_FIELD.name)
                .constrainAs(startRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(memberRef.bottom, margin = 8.dp)

                    width = Dimension.fillToConstraints
                },
            field = state.hourStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_time_start),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_start_hour)
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
                    Firebase.analytics.logButtonClick(COMPROMISE_SCREEN_SAVE_KEYBOARD_DONE)
                    onKeyboardDone()
                }
            )
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromisePreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            UniqueCompromise(
                state = compromiseAcademyMemberEditionState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromisePreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            UniqueCompromise(
                state = compromiseAcademyMemberEditionState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromiseCanceledPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            UniqueCompromise(
                state = compromiseAcademyMemberCancelatedState
            )
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun UniqueCompromiseCanceledPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            UniqueCompromise(
                state = compromiseAcademyMemberCancelatedState
            )
        }
    }
}