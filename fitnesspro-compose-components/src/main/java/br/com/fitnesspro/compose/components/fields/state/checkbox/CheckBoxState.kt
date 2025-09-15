package br.com.fitnesspro.compose.components.fields.state.checkbox

data class CheckBoxState(
    val label: String,
    val identifier: Enum<*>,
    val checked: Boolean = false,
    val enabled: Boolean = true
)