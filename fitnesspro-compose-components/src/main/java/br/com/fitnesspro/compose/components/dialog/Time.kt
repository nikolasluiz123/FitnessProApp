package br.com.fitnesspro.compose.components.dialog

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.buttons.FitnessProTextButton
import br.com.fitnesspro.compose.components.dialog.enums.EnumDatePickerDialogTestTags.DATE_PICKER_DIALOG
import br.com.fitnesspro.compose.components.dialog.enums.EnumDatePickerDialogTestTags.DATE_PICKER_DIALOG_BUTTON_CANCEL
import br.com.fitnesspro.compose.components.dialog.enums.EnumDatePickerDialogTestTags.DATE_PICKER_DIALOG_BUTTON_CONFIRM
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG_BUTTON_CANCEL
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG_BUTTON_CONFIRM
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_DIALOG_TITLE
import br.com.fitnesspro.compose.components.dialog.enums.EnumTimePickerInputTestTags.TIME_PICKER_INPUT
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.theme.DialogTitleTextStyle
import br.com.fitnesspro.core.theme.GREY_200
import br.com.fitnesspro.core.theme.GREY_300
import br.com.fitnesspro.core.theme.GREY_800
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessProDatePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (LocalDate) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        modifier = modifier.testTag(DATE_PICKER_DIALOG.name),
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FitnessProTextButton(
                modifier = Modifier.testTag(DATE_PICKER_DIALOG_BUTTON_CONFIRM.name),
                label = stringResource(id = R.string.label_confirm),
                onClickListener = {
                    datePickerState.selectedDateMillis?.let {
                        val localDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        onConfirm(localDate)
                    }
                }
            )
        },
        dismissButton = {
            FitnessProTextButton(
                modifier = Modifier.testTag(DATE_PICKER_DIALOG_BUTTON_CANCEL.name),
                label = stringResource(id = R.string.label_cancel),
                onClickListener = onCancel
            )
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String,
    value: LocalTime? = null,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val time = value ?: timeNow()

    val timePickerState = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true,
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .testTag(TIME_PICKER_DIALOG.name)
            .wrapContentHeight(),
        properties = properties
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = GREY_200
        ) {
            ConstraintLayout(
                Modifier.padding(16.dp)
            ) {
                val (titleRef, inputRef, buttonsRef) = createRefs()

                Text(
                    modifier = Modifier
                        .testTag(TIME_PICKER_DIALOG_TITLE.name)
                        .constrainAs(titleRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        },
                    text = title,
                    style = DialogTitleTextStyle,
                    color = GREY_800
                )

                TimeInput(
                    modifier = Modifier
                        .testTag(TIME_PICKER_INPUT.name)
                        .focusable()
                        .padding(top = 16.dp)
                        .constrainAs(inputRef) {
                            start.linkTo(parent.start)
                            top.linkTo(titleRef.bottom)
                            end.linkTo(parent.end)
                            bottom.linkTo(buttonsRef.top)
                        },
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        timeSelectorSelectedContentColor = GREY_800,
                        timeSelectorSelectedContainerColor = GREY_200,
                        timeSelectorUnselectedContainerColor = GREY_300,
                        periodSelectorBorderColor = GREY_200,
                        periodSelectorSelectedContentColor = GREY_800,
                        periodSelectorSelectedContainerColor = GREY_200,
                        clockDialSelectedContentColor = GREY_300,
                        clockDialColor = GREY_300

                    )
                )

                Row(
                    modifier = Modifier.constrainAs(buttonsRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)

                        width = Dimension.fillToConstraints
                    },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    FitnessProTextButton(
                        modifier = Modifier.testTag(TIME_PICKER_DIALOG_BUTTON_CANCEL.name),
                        label = stringResource(id = R.string.label_cancel),
                        onClickListener = {
                            onDismiss()
                        }
                    )

                    FitnessProTextButton(
                        modifier = Modifier.testTag(TIME_PICKER_DIALOG_BUTTON_CONFIRM.name),
                        label = stringResource(id = R.string.label_confirm),
                        onClickListener = {
                            onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}