package br.com.fitnesspro.compose.components.state

data class Field(
    val value: String = "",
    val onChange: (String) -> Unit = { },
    val errorMessage: String = ""
) {

    fun valueIsEmpty() = value.isEmpty()
}