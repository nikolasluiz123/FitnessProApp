package br.com.fitnesspro.scheduler.ui.screen.compromisse

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.android.ui.compose.components.fields.text.OutlinedTextFieldValidation
import br.com.android.ui.compose.components.fields.text.date.DatePickerOutlinedTextFieldValidation
import br.com.android.ui.compose.components.fields.text.dialog.paged.PagedListDialogOutlinedTextFieldValidation
import br.com.android.ui.compose.components.fields.text.time.TimePickerOutlinedTextFieldValidation
import br.com.fitnesspro.compose.components.fields.FitnessProDayWeeksSelector
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_DAY_WEEKS_SELECTOR
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_DATE_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_END_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_MEMBER_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_OBSERVATION_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_DATE_FIELD
import br.com.fitnesspro.scheduler.ui.screen.compromisse.enums.EnumCompromiseScreenTags.COMPROMISE_SCREEN_START_HOUR_FIELD
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState

@Composable
internal fun RecurrentCompromise(state: CompromiseUIState) {
    ConstraintLayout(
        Modifier
            .padding(8.dp)
            .fillMaxSize()
            .imePadding()
    ) {
        val (memberRef, startDateRef, endDateRef, startHourRef, endHourRef,
            observationRef, dayWeeksRef) = createRefs()

        PagedListDialogOutlinedTextFieldValidation(
            field = state.member,
            fieldLabel = stringResource(R.string.compromise_screen_label_member),
            simpleFilterPlaceholderResId = R.string.compromise_screen_simple_filter_placeholder_member_dialog_list,
            emptyMessage = R.string.compromise_screen_empty_message_member_dialog_list,
            itemLayout = { personTuple ->
                PersonDialogListItem(
                    person = personTuple,
                    onItemClick = state.member.dialogListState.onDataListItemClick
                )
            },
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_MEMBER_FIELD.name)
                .constrainAs(memberRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        DatePickerOutlinedTextFieldValidation(
            field = state.dateStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_start_date),
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_START_DATE_FIELD.name)
                .constrainAs(startDateRef) {
                    top.linkTo(memberRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        DatePickerOutlinedTextFieldValidation(
            field = state.dateEnd,
            fieldLabel = stringResource(R.string.compromise_screen_label_end_date),
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_END_DATE_FIELD.name)
                .constrainAs(endDateRef) {
                    top.linkTo(startDateRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )


        TimePickerOutlinedTextFieldValidation(
            field = state.hourStart,
            fieldLabel = stringResource(R.string.compromise_screen_label_start_hour),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_start_hour),
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_START_HOUR_FIELD.name)
                .constrainAs(startHourRef) {
                    top.linkTo(endDateRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        TimePickerOutlinedTextFieldValidation(
            field = state.hourEnd,
            fieldLabel = stringResource(R.string.compromise_screen_label_end_hour),
            timePickerTitle = stringResource(R.string.compromise_screen_time_picker_title_end_hour),
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_END_HOUR_FIELD.name)
                .constrainAs(endHourRef) {
                    top.linkTo(startHourRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        OutlinedTextFieldValidation(
            field = state.observation,
            label = stringResource(R.string.compromise_screen_label_observation),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_OBSERVATION_FIELD.name)
                .constrainAs(observationRef) {
                    top.linkTo(endHourRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )

        FitnessProDayWeeksSelector(
            selectorField = state.dayWeeksSelectorField,
            modifier = Modifier
                .testTag(COMPROMISE_SCREEN_DAY_WEEKS_SELECTOR.name)
                .fillMaxWidth()
                .constrainAs(dayWeeksRef) {
                    top.linkTo(observationRef.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)

                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun RecurrentCompromisePreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            RecurrentCompromise(
                state = compromisePersonalRecurrentState
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun RecurrentCompromisePreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            RecurrentCompromise(
                state = compromisePersonalRecurrentState
            )
        }
    }
}