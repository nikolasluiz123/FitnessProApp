package br.com.fitnesspro.compose.components.fields.state.radiobutton

data class RadioButtonState(
    val label: String,
    val identifier: Enum<*>,
    val selected: Boolean = false,
    val enabled: Boolean = true
)
