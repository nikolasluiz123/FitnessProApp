package br.com.fitnesspro.compose.components.fields.menu

data class MenuItem<T>(
    val label: String,
    val value: T,
    var selected: Boolean = false
)
