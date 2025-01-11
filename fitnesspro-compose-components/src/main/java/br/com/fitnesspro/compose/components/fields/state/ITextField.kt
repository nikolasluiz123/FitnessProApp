package br.com.fitnesspro.compose.components.fields.state

interface ITextField {
    val value: String
    val onChange: (String) -> Unit
    val errorMessage: String
}