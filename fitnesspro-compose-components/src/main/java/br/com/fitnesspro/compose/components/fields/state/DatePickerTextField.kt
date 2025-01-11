package br.com.fitnesspro.compose.components.fields.state

import java.time.LocalDate

data class DatePickerTextField(
    val datePickerOpen: Boolean = false,
    val onDatePickerOpenChange: (Boolean) -> Unit = { },
    val onDateChange: (LocalDate) -> Unit = { },
    val onDatePickerDismiss: () -> Unit = { },
    override val value: String = "",
    override val onChange: (String) -> Unit = { },
    override val errorMessage: String = ""
): ITextField