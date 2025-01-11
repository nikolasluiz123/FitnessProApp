package br.com.fitnesspro.compose.components.fields.state

data class SwitchButtonField(
    var checked: Boolean = false,
    val onCheckedChange: (Boolean) -> Unit = { },
    val enabled: Boolean = true
)