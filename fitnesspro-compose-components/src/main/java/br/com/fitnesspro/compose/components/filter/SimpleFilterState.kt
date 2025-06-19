package br.com.fitnesspro.compose.components.filter

data class SimpleFilterState(
    val quickFilter: String = "",
    val simpleFilterExpanded: Boolean = false,
    val onSimpleFilterChange: (String) -> Unit = { },
    val onExpandedChange: (Boolean) -> Unit = { }
)