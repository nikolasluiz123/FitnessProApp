package br.com.fitnesspro.compose.components.fields.state.checkbox

data class MultipleCheckBoxesState(
    val checkBoxes: List<CheckBoxState> = emptyList(),
    val onCheckBoxClick: (Enum<*>) -> Unit = { },
    val maxColumns: Int = 3
)