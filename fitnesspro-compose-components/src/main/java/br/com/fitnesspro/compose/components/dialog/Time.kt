package br.com.fitnesspro.compose.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.GREY_200
import br.com.fitnesspro.core.theme.GREY_300
import br.com.fitnesspro.core.theme.GREY_800
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerInput(
    title: String,
    value: LocalTime? = null,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val time = value ?: LocalTime.now()

    val timePickerState = rememberTimePickerState(
        initialHour = time.hour,
        initialMinute = time.minute,
        is24Hour = true,
    )

    TimePickerDialog(
        title = title,
        onDismiss = { onDismiss() },
        onConfirm = {
            onConfirm(LocalTime.of(timePickerState.hour, timePickerState.minute))
            onDismiss()
        }
    ) {
        TimeInput(
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
    }
}
@Composable
fun TimePickerDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        title = {
            Text(title)
        },
        containerColor = GREY_200,
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(id = R.string.label_cancel))
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(stringResource(id = R.string.label_confirm))
            }
        },
        text = { content() }
    )
}