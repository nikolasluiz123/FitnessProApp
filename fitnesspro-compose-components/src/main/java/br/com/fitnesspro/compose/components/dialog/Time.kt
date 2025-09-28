package br.com.fitnesspro.compose.components.dialog

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.buttons.FitnessProTextButton
import br.com.fitnesspro.core.extensions.timeNow
import br.com.fitnesspro.core.theme.DialogTitleTextStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

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
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            FitnessProTextButton(
                label = stringResource(id = R.string.label_confirm),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                onClickListener = {
                    datePickerState.selectedDateMillis?.let {
                        val localDate = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                        onConfirm(localDate)
                    }
                }
            )
        },
        dismissButton = {
            FitnessProTextButton(
                label = stringResource(id = R.string.label_cancel),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
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
    val time = value ?: timeNow(ZoneId.systemDefault())

    val timePickerState = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true,
    )

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .wrapContentHeight(),
        properties = properties
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            ConstraintLayout(
                Modifier.padding(16.dp)
            ) {
                val (titleRef, inputRef, buttonsRef) = createRefs()

                Text(
                    modifier = Modifier
                        .constrainAs(titleRef) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                        },
                    text = title,
                    style = DialogTitleTextStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TimeInput(
                    modifier = Modifier
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
                        timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onSurface,
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        label = stringResource(id = R.string.label_cancel),
                        onClickListener = {
                            onDismiss()
                        }
                    )

                    FitnessProTextButton(
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
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

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDatePickerDialogPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProDatePickerDialog(
                onDismissRequest = { },
                onConfirm = { },
                onCancel = { }
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun TimePickerDialogPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            TimePickerDialog(
                title = "Title",
                onDismiss = { },
                onConfirm = { }
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProDatePickerDialogPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProDatePickerDialog(
                onDismissRequest = { },
                onConfirm = { },
                onCancel = { }
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun TimePickerDialogPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            TimePickerDialog(
                title = "Title",
                onDismiss = { },
                onConfirm = { }
            )
        }
    }
}