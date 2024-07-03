package br.com.fitnesspro.compose.components.menu

data class MenuItem<T>(
    val label: String,
    val value: T
)
