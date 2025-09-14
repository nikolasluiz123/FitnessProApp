package br.com.fitnesspro.compose.components.fields.state.radiobutton

data class MultipleRadioButtonsState(
    val radioButtons: List<RadioButtonState> = emptyList(),
    val onRadioButtonClick: (Enum<*>) -> Unit = { },
    val maxColumns: Int = 3
)