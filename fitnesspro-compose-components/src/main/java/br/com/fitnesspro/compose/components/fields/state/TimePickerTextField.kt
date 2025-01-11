package br.com.fitnesspro.compose.components.fields.state

import java.time.LocalTime

data class TimePickerTextField(
    val timePickerOpen: Boolean = false,
    val onTimePickerOpenChange: (Boolean) -> Unit = { },
    val onTimeChange: (LocalTime) -> Unit = { },
    val onTimeDismiss: () -> Unit = { },
    override val value: String = "",
    override val onChange: (String) -> Unit = { },
    override val errorMessage: String = ""
): ITextField