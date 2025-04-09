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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.fields.OutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.PagedListDialogOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.TimePickerOutlinedTextFieldValidation
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState

@Composable
fun UniqueCompromise(state: CompromiseUIState) {
    val scrollState = rememberScrollState()

    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .scrollable(scrollState, orientation = Orientation.Vertical)
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